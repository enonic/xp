package com.enonic.wem.api.module;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

public final class ModuleKey
{
    private static final String SEPARATOR = "-";

    private final ModuleName name;

    private final ModuleVersion version;

    private ModuleKey( final ModuleName name, final ModuleVersion version )
    {
        Preconditions.checkNotNull( name );
        Preconditions.checkNotNull( version );
        this.name = name;
        this.version = version;
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
        return name.equals( that.name ) && version.equals( that.version );
    }

    @Override
    public int hashCode()
    {
        int result = name.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return name.toString() + SEPARATOR + version.toString();
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
