package com.enonic.wem.api.module;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

public final class ModuleKey
{
    private static final String SEPARATOR = "-";

    private final ModuleName name;

    private final ModuleVersion version;

    private final String refString;

    private ModuleKey( final ModuleName name, final ModuleVersion version )
    {
        Preconditions.checkNotNull( name );
        Preconditions.checkNotNull( version );
        this.name = name;
        this.version = version;
        this.refString = name.toString() + SEPARATOR + version.toString();
    }

    public ModuleName getName()
    {
        return name;
    }

    public ModuleVersion getVersion()
    {
        return version;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || !( o instanceof ModuleKey ) )
        {
            return false;
        }
        final ModuleKey that = (ModuleKey) o;
        return refString.equals( that.refString );
    }

    @Override
    public int hashCode()
    {
        return refString.hashCode();
    }

    @Override
    public String toString()
    {
        return refString;
    }

    public static ModuleKey from( final ModuleName name, final ModuleVersion version )
    {
        return new ModuleKey( name, version );
    }

    public static ModuleKey from( final String moduleKey )
    {
        final String name = StringUtils.substringBefore( moduleKey, SEPARATOR );
        final String version = StringUtils.substringAfter( moduleKey, SEPARATOR );
        return new ModuleKey( ModuleName.from( name ), ModuleVersion.from( version ) );
    }
}
