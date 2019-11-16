package com.enonic.xp.migration;

public class StringUtils
{

    public static String substringAfterLast( String str, String separator )
    {
        return org.apache.commons.lang.StringUtils.substringAfterLast( str, separator );
    }

    public static String substringBeforeLast( String str, String separator )
    {
        return org.apache.commons.lang.StringUtils.substringBeforeLast( str, separator );
    }

    public static String substringBefore( String str, String separator )
    {
        return org.apache.commons.lang.StringUtils.substringBefore( str, separator );
    }

    public static String substringAfter( String str, String separator )
    {
        return org.apache.commons.lang.StringUtils.substringAfter( str, separator );
    }

    public static String substringBetween( String str, String open, String close )
    {
        return org.apache.commons.lang.StringUtils.substringBetween( str, open, close );
    }

    public static String substringBetween( String str, String tag )
    {
        return org.apache.commons.lang.StringUtils.substringBetween( str, tag );
    }

    public static boolean containsAny( String str, char[] searchChars )
    {
        return org.apache.commons.lang.StringUtils.containsAny( str, searchChars );
    }

    public static String abbreviate( String str, int maxWidth )
    {
        return org.apache.commons.lang.StringUtils.abbreviate( str, maxWidth );
    }

    public static boolean containsIgnoreCase( String str, String searchStr )
    {
        return org.apache.commons.lang.StringUtils.containsIgnoreCase( str, searchStr );
    }
}
