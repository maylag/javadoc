package com.github.setial.intellijjavadocs.configuration;

import com.intellij.openapi.components.ApplicationComponent;

/**
 * The interface Java doc configuration.
 *
 * @author Sergey Timofiychuk
 */
public interface JavaDocConfiguration extends ApplicationComponent {

    /**
     * The constant COMPONENT_VERSION.
     */
    String COMPONENT_VERSION = "2.1.0";

    /**
     * The constant COMPONENT_NAME.
     */
    String COMPONENT_NAME = "JavaDocConfiguration_" + COMPONENT_VERSION;
}
