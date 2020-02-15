package com.github.setial.intellijjavadocs.generator.impl;

import com.github.setial.intellijjavadocs.generator.JavaDocGenerator;
import com.github.setial.intellijjavadocs.model.JavaDoc;
import com.github.setial.intellijjavadocs.model.settings.JavaDocSettings;
import com.github.setial.intellijjavadocs.model.settings.Mode;
import com.github.setial.intellijjavadocs.model.settings.Visibility;
import com.github.setial.intellijjavadocs.template.DocTemplateManager;
import com.github.setial.intellijjavadocs.template.DocTemplateProcessor;
import com.github.setial.intellijjavadocs.utils.JavaDocUtils;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.project.Project;
import com.intellij.pom.PomNamedTarget;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.SystemProperties;
import com.intellij.util.text.DateFormatUtil;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Abstract java doc generator.
 *
 * @param <T> the type parameter
 * @author Sergey Timofiychuk
 */
public abstract class AbstractJavaDocGenerator<T extends PsiElement> implements JavaDocGenerator<T> {

    private final String DATE_FORMAT = "yyyy/MM/dd";

    private final String VERSION = "VERSION";

    private final String BASE_VERSION = "BASE_VERSION";

    private final Project project;

    private DocTemplateManager docTemplateManager;

    private DocTemplateProcessor docTemplateProcessor;

    private PsiElementFactory psiElementFactory;

    /**
     * Instantiates a new Abstract java doc generator.
     *
     * @param project the Project
     */
    public AbstractJavaDocGenerator(@NotNull Project project) {
        this.project = project;
        docTemplateManager = ApplicationManager.getApplication().getComponent(DocTemplateManager.class);
        docTemplateProcessor = project.getComponent(DocTemplateProcessor.class);
        psiElementFactory = PsiElementFactory.getInstance(project);
    }

    @Nullable
    @Override
    public final PsiDocComment generate(@NotNull T element) {
        PsiDocComment result = null;
        PsiDocComment oldDocComment = null;
        PsiElement firstElement = element.getFirstChild();
        if (firstElement instanceof PsiDocComment) {
            oldDocComment = (PsiDocComment) firstElement;
        }

        JavaDocSettings configuration = JavaDocSettings.getInstance();
        if (configuration != null) {
            Mode mode = configuration.getGeneralSettings().getMode();
            switch (mode) {
                case KEEP:
                    if (oldDocComment != null) {
                        break;
                    }
                case REPLACE:
                    result = replaceJavaDocAction(element);
                    break;
                case UPDATE:
                default:
                    if (oldDocComment != null) {
                        result = updateJavaDocAction(element, oldDocComment);
                    } else {
                        result = replaceJavaDocAction(element);
                    }
                    break;
            }
        }
        return result;
    }

    /**
     * Gets the doc template manager.
     *
     * @return the Doc template manager
     */
    @NotNull
    protected DocTemplateManager getDocTemplateManager() {
        return docTemplateManager;
    }

    /**
     * Gets the doc template processor.
     *
     * @return the Doc template processor
     */
    @NotNull
    protected DocTemplateProcessor getDocTemplateProcessor() {
        return docTemplateProcessor;
    }

    /**
     * Gets the psi element factory.
     *
     * @return the Psi element factory
     */
    @NotNull
    protected PsiElementFactory getPsiElementFactory() {
        return psiElementFactory;
    }

    /**
     * Check whether javadoc should be generated.
     *
     * @param modifiers the modifiers
     * @return the boolean
     */
    protected boolean shouldGenerate(PsiModifierList modifiers) {
        return checkModifiers(modifiers, PsiModifier.PUBLIC, Visibility.PUBLIC) || checkModifiers(modifiers,
                PsiModifier.PROTECTED, Visibility.PROTECTED) || checkModifiers(modifiers, PsiModifier.PACKAGE_LOCAL,
                Visibility.DEFAULT) || checkModifiers(modifiers, PsiModifier.PRIVATE, Visibility.PRIVATE);
    }

    private String getCalendarValue(final Calendar calendar, final int field) {
        int val = calendar.get(field);
        if (field == Calendar.MONTH) {
            val++;
        }
        final String result = Integer.toString(val);
        if (result.length() == 1) {
            return "0" + result;
        }
        return result;
    }

    /**
     * Gets default parameters used to build template.
     *
     * @param element the element
     * @return the default parameters
     */
    protected Map<String, Object> getDefaultParameters(PomNamedTarget element) {
        Map<String, Object> params = new HashMap<String, Object>();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdfMonthNameShort = new SimpleDateFormat("MMM");
        SimpleDateFormat sdfMonthNameFull = new SimpleDateFormat("MMMM");
        SimpleDateFormat sdfDayNameShort = new SimpleDateFormat("EEE");
        SimpleDateFormat sdfDayNameFull = new SimpleDateFormat("EEEE");
        SimpleDateFormat sdfYearFull = new SimpleDateFormat("yyyy");
        // 下面这些都是intellij 内部的变量名称
        params.put("DATE", DateFormatUtil.formatDate(date));
        params.put("TIME", DateFormatUtil.formatTime(date));
        params.put("YEAR", sdfYearFull.format(date));
        params.put("MONTH", getCalendarValue(calendar, Calendar.MONTH));
        params.put("MONTH_NAME_SHORT", sdfMonthNameShort.format(date));
        params.put("MONTH_NAME_FULL", sdfMonthNameFull.format(date));
        params.put("DAY", getCalendarValue(calendar, Calendar.DAY_OF_MONTH));
        params.put("DAY_NAME_SHORT", sdfDayNameShort.format(date));
        params.put("DAY_NAME_FULL", sdfDayNameFull.format(date));
        params.put("HOUR", getCalendarValue(calendar, Calendar.HOUR_OF_DAY));
        params.put("MINUTE", getCalendarValue(calendar, Calendar.MINUTE));
        params.put("SECOND", getCalendarValue(calendar, Calendar.SECOND));

        params.put("USER", SystemProperties.getUserName());
        params.put("PRODUCT_NAME", ApplicationNamesInfo.getInstance().getFullProductName());
        params.put("DS", "$"); // Dollar sign, strongly needed for PHP, JS, etc. See WI-8979
        params.put("PROJECT_NAME", project.getName());

        params.put("element", element);
        params.put("name", getDocTemplateProcessor().buildDescription(element.getName(), true));
        params.put("partName", getDocTemplateProcessor().buildPartialDescription(element.getName()));
        params.put("splitNames", StringUtils.splitByCharacterTypeCamelCase(element.getName()));

        params.put("NOW", DateFormatUtils.format(date, DATE_FORMAT));
        params.put("AUTHOR", SystemProperties.getUserName());

        // 处理变量
        for (Map.Entry<String, String> variable : getDocTemplateManager().getVariables().entrySet()) {
            params.put(variable.getKey(), variable.getValue());
        }
        // 处理version
        String version = (String) params.get(VERSION);
        params.put(VERSION, StringUtils.isEmpty(version) ? "1.0" : version);
        String baseVersion = (String) params.get(BASE_VERSION);
        params.put(BASE_VERSION, StringUtils.isEmpty(baseVersion) ? params.get(VERSION) : baseVersion);

        return params;
    }

    private PsiDocComment updateJavaDocAction(T element, PsiDocComment oldDocComment) {
        PsiDocComment result = null;
        JavaDoc newJavaDoc = generateJavaDoc(element);
        JavaDoc oldJavaDoc = JavaDocUtils.createJavaDoc(oldDocComment);
        if (newJavaDoc != null) {
            newJavaDoc = JavaDocUtils.mergeJavaDocs(oldJavaDoc, newJavaDoc);
            String javaDoc = newJavaDoc.toJavaDoc();
            result = psiElementFactory.createDocCommentFromText(javaDoc);
        }
        return result;
    }

    private PsiDocComment replaceJavaDocAction(T element) {
        PsiDocComment result = null;
        JavaDoc newJavaDoc = generateJavaDoc(element);
        if (newJavaDoc != null) {
            String javaDoc = newJavaDoc.toJavaDoc();
            result = psiElementFactory.createDocCommentFromText(javaDoc);
        }
        return result;
    }

    private boolean checkModifiers(PsiModifierList modifiers, String modifier, Visibility visibility) {
        JavaDocSettings configuration = JavaDocSettings.getInstance();
        return modifiers != null && modifiers.hasModifierProperty(modifier) && configuration != null
                && configuration.getGeneralSettings().getVisibilities().contains(visibility);
    }

    /**
     * Generate java doc.
     *
     * @param element the Element
     * @return the Java doc
     */
    @Nullable
    protected abstract JavaDoc generateJavaDoc(@NotNull T element);

}
