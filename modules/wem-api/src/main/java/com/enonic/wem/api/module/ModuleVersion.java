package com.enonic.wem.api.module;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

public final class ModuleVersion
{
    private static final String SEPARATOR = "-";

    private final ModuleName name;

    private final Version version;

    public ModuleVersion( final ModuleName name, final Version version )
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

    public Version getVersion()
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
        if ( o == null || !( o instanceof ModuleVersion ) )
        {
            return false;
        }
        final ModuleVersion that = (ModuleVersion) o;
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

    public static ModuleVersion parse( final String moduleVersion )
    {
        final String name = StringUtils.substringBefore( moduleVersion, SEPARATOR );
        final String version = StringUtils.substringAfter( moduleVersion, SEPARATOR );
        return new ModuleVersion( ModuleName.from( name ), Version.from( version ) );
    }
}
