/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class FilterExprParserContext
{
    private static final Pattern QUOTE_PATTERN = Pattern.compile( "'([^']*)'" );

    private static final Pattern DQUOTE_PATTERN = Pattern.compile( "\"([^\"]*)\"" );

    private final String value;

    private final HashMap<String, String> stringMap;

    public FilterExprParserContext( String value )
    {
        this.stringMap = new HashMap<>();
        this.value = replaceStrings( this.stringMap, value.trim() );
    }

    public FilterSetExpr parseFilterSet()
    {
        return new FilterSetExpr( Arrays.stream( this.value.split( ";" ) ).
            map( this::parseFilter ).
            filter( Objects::nonNull ).
            collect( Collectors.toUnmodifiableList() ) );
    }

    private FilterExpr parseFilter( String str )
    {
        String name = parseName( str );

        if ( name != null )
        {
            return new FilterExpr( name, parseArguments( str ) );
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
            return new Object[0];
        }

        int beginPos = str.indexOf( '(' );
        int endPos = str.indexOf( ')' );

        if ( ( beginPos < 0 ) || ( endPos < 0 ) )
        {
            return new Object[0];
        }

        str = str.substring( beginPos + 1, endPos ).trim();
        if ( str.length() == 0 )
        {
            return new Object[0];
        }

        ArrayList<Object> list = new ArrayList<>();
        for ( String part : str.split( "," ) )
        {
            list.add( parseValue( part ) );
        }

        return list.toArray();
    }

    private Object parseValue( String str )
    {
        if ( str == null )
        {
            return null;
        }

        str = str.trim();
        if ( str.isEmpty() )
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
        if ( "true".equals( str ) )
        {
            return Boolean.TRUE;
        }
        else if ( "false".equals( str ) )
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
            return Double.valueOf( str );
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
            return Integer.valueOf( str );
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
