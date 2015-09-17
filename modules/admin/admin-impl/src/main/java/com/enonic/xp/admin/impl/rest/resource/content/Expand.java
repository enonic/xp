package com.enonic.xp.admin.impl.rest.resource.content;

public enum Expand
{
    FULL,
    SUMMARY,
    NONE;

    public boolean matches( final String value )
    {
        return this.name().equalsIgnoreCase( value );
    }
}
