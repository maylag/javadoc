package com.github.maylag.javadoc.test;

import java.util.List;
import java.util.Map;

/**
 * The type Main.
 *
 * @since 2017-09-03 14:01:30
 */
public class Main {

    /**
     * The Instance.
     */
    private Main instance = new Main();

    /**
     * The __ aa.
     */
    protected String __aa = "";

    /**
     * The Acdf.
     */
    public final String ACDF = "AAAA";

    /**
     * The Aer.
     */
    private final String _aer = "";

    /**
     * The Is bool.
     */
    private volatile boolean isBool = true;

    /**
     * The List string.
     */
    protected List<String> listString = null;

    /**
     * Gets env.
     *
     * @return string the env
     */
    public static native String getEnv();

    /**
     * The M.
     */
    protected  int m = 0;

    /**
     * The Abc.
     */
    public String abc = "";

    /**
     * The constant AAA.
     */
    private static String AAA = "aaa";


    /**
     * The constant abcd.
     */
    private static final Map<String, String> abcd = null;

    /**
     * Main.
     *
     * @param args the args
     * @param a    the a
     * @param i    the
     * @throws Exception the exception
     * @throws Throwable the throwable
     */
    public static void main(String[] args, String a, Integer i) throws Exception, Throwable {
        System.out.println("Hello World!");
    }

    /**
     * Test string.
     *
     * @param a the a
     * @return the string
     */
    private String test(int a)
    {
        return "abc";
    }

    /**
     * The interface Testintf.
     *
     * @since 2017-09-03 14:01:30
     */
    public interface testintf
    {
        /**
         * Calc int.
         *
         * @param a the a
         * @param b the b
         * @return the int
         */
        public int calc(int a, int b);
    }

    /**
     * Instantiates a new Main.
     */
    public Main(){}

    /**
     * Instantiates a new Main.
     *
     * @param a the a
     */
    public Main(String a)
    {
        this.abc = a;
    }

    /**
     * The enum Test enum.
     *
     * @since 2017-09-03 14:01:30
     */
    protected enum testEnum {
        /**
         * Test 1 test enum.
         */
        TEST1,
        /**
         * Test 2 test enum.
         */
        TEST2
    }
}

