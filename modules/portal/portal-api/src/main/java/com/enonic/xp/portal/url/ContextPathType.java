package com.enonic.xp.portal.url;

import java.util.Arrays;

public enum ContextPathType
{
    VHOST( "vhost" ), RELATIVE( "relative" );

    private final String value;

    ContextPathType( final String value )
    {
        this.value = value;
    }

    public static ContextPathType from( final String value )
    {
        return Arrays.stream( ContextPathType.values() ).
            filter( contextPathEnum -> contextPathEnum.value.equals( value ) ).
            findFirst().
            get();
    }

    public String getValue()
    {
        return value;
    }
}
