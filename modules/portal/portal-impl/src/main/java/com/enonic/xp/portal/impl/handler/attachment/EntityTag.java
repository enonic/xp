package com.enonic.xp.portal.impl.handler.attachment;

import java.util.Arrays;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record EntityTag(String value)
{
    private static final String WEAK_PREFIX = "W/";

    public String toHeaderValue()
    {
        return "\"" + value + "\"";
    }

    public boolean matchesIfNoneMatch( final String headerValue )
    {
        return Arrays.stream( headerValue.split( "," ) ).map( String::trim ).map( EntityTag::stripWeakPrefix ).anyMatch( this::isHeaderValue );
    }

    public boolean matchesIfRange( final String headerValue )
    {
        return isHeaderValue( headerValue.trim() );
    }

    private boolean isHeaderValue( final String tag )
    {
        return toHeaderValue().equals( tag );
    }

    private static String stripWeakPrefix( final String tag )
    {
        return tag.startsWith( WEAK_PREFIX ) ? tag.substring( WEAK_PREFIX.length() ) : tag;
    }
}
