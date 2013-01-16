/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FilterExprParserContext
{
    private final static Pattern QUOTE_PATTERN = Pattern.compile( "'([^']*)'" );

    private final static Pattern DQUOTE_PATTERN = Pattern.compile( "\"([^\"]*)\"" );

    private final String value;

    private final HashMap<String, String> stringMap;

    public FilterExprParserContext( String value )
    {
        this.stringMap = new HashMap<String, String>();
        this.value = replaceStrings( this.stringMap, value );
    }

    public FilterSetExpr parseFilterSet()
    {
        FilterSetExpr set = new FilterSetExpr();
        for ( String part : this.value.split( ";" ) )
        {
            FilterExpr expr = parseFilter( part );
            if ( expr != null )
            {
                set.add( expr );
            }
        }

        return set;
    }

    private FilterExpr parseFilter( String str )
    {
        String name = parseName( str );
        Object[] args = parseArguments( str );

        if ( name != null )
        {
            return new FilterExpr( name, args );
        }
        else
        {
            return null;
        }
    }

    private String parseName( String str )
    {
        if ( str == null )
        {
            return null;
        }

        int pos = str.indexOf( '(' );
        if ( pos >= 0 )
        {
            str = str.substring( 0, pos );
        }

        str = str.trim();
        if ( str.length() > 0 )
        {
            return str;
        }
        else
        {
            return null;
        }
    }

    private Object[] parseArguments( String str )
    {
        if ( str == null )
        {
            return null;
        }

        int beginPos = str.indexOf( '(' );
        int endPos = str.indexOf( ')' );

        if ( ( beginPos < 0 ) || ( endPos < 0 ) )
        {
            return null;
        }

        str = str.substring( beginPos + 1, endPos ).trim();
        if ( str.length() == 0 )
        {
            return null;
        }

        ArrayList<Object> list = new ArrayList<Object>();
        for ( String part : str.split( "," ) )
        {
            list.add( parseValue( part ) );
        }

        return list.toArray( new Object[list.size()] );
    }

    private Object parseValue( String str )
    {
        if ( str == null )
        {
            return null;
        }

        str = str.trim();
        if ( str.length() == 0 )
        {
            return null;
        }

        Object value = parseStringValue( str );
        if ( value == null )
        {
            value = parseHexValue( str );
        }

        if ( value == null )
        {
            value = parseBooleanValue( str );
        }

        if ( value == null )
        {
            value = parseIntegerValue( str );
        }

        if ( value == null )
        {
            value = parseDoubleValue( str );
        }

        return value;
    }

    private String parseStringValue( String str )
    {
        if ( str.startsWith( "%" ) )
        {
            return this.stringMap.get( str );
        }
        else
        {
            return null;
        }
    }

    private Integer parseHexValue( String str )
    {
        if ( !str.startsWith( "0x" ) )
        {
            return null;
        }

        str = str.substring( 2 );

        try
        {
            return Integer.parseInt( str, 16 );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    private Boolean parseBooleanValue( String str )
    {
        if ( str.equals( "true" ) )
        {
            return Boolean.TRUE;
        }
        else if ( str.equals( "false" ) )
        {
            return Boolean.FALSE;
        }
        else
        {
            return null;
        }
    }

    private Double parseDoubleValue( String str )
    {
        try
        {
            return new Double( str );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    private Integer parseIntegerValue( String str )
    {
        try
        {
            return new Integer( str );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    private static String replaceStrings( HashMap<String, String> map, String value )
    {
        return replaceStrings( map, replaceStrings( map, value, QUOTE_PATTERN ), DQUOTE_PATTERN );
    }

    private static String replaceStrings( HashMap<String, String> map, String value, Pattern pattern )
    {
        StringBuffer str = new StringBuffer();
        Matcher matcher = pattern.matcher( value );

        while ( matcher.find() )
        {
            matcher.appendReplacement( str, addReplacementValue( map, matcher.group( 1 ) ) );
        }

        matcher.appendTail( str );
        return str.toString();
    }

    private static String addReplacementValue( HashMap<String, String> map, String value )
    {
        String key = "%" + map.size();
        map.put( key, value );
        return key;
    }
}
