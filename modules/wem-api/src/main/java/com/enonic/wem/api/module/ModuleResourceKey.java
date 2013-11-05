package com.enonic.wem.api.module;


import java.util.Objects;

import org.apache.commons.lang.StringUtils;

public class ModuleResourceKey
{
    private static final String SEPARATOR = ":";

    private final ModuleKey moduleKey;

    private final ResourcePath path;

    private final String refString;

    public ModuleResourceKey( final ModuleKey moduleKey, final ResourcePath path )
    {
        this.moduleKey = moduleKey;
        this.path = path;
        this.refString = moduleKey.toString() + SEPARATOR + path.toString();
    }

    public ModuleKey getModuleKey()
    {
        return moduleKey;
    }

    public ResourcePath getPath()
    {
        return path;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final ModuleResourceKey that = (ModuleResourceKey) o;
        return Objects.equals( this.refString, that.refString );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.refString );
    }

    public String toString()
    {
        return refString;
    }

    public static ModuleResourceKey from( final String moduleResourceKey )
    {
        final String moduleKey = StringUtils.substringBefore( moduleResourceKey, SEPARATOR );
        final String resourcePath = StringUtils.substringAfter( moduleResourceKey, SEPARATOR );
        return new ModuleResourceKey( ModuleKey.from( moduleKey ), ResourcePath.from( resourcePath ) );
    }
}
