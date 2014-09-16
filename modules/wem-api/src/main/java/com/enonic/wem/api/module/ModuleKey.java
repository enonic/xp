package com.enonic.wem.api.module;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.google.common.base.Preconditions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static org.apache.commons.lang.StringUtils.substringAfterLast;
import static org.apache.commons.lang.StringUtils.substringBeforeLast;

public final class ModuleKey
{

    private static final String SYSTEM_MODULE_NAME = "system";

    public final static ModuleKey SYSTEM = new ModuleKey( ModuleName.from( SYSTEM_MODULE_NAME ), new ModuleVersion( "0.0.0" ) );

    private static final String SEPARATOR = "-";

    private final ModuleName name;

    private final ModuleVersion version;

    private final String refString;

    private ModuleKey( final ModuleName name, final ModuleVersion version )
    {
        checkNotNull( name );
        checkNotNull( version );
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
        final String name = substringBeforeLast( moduleKey, SEPARATOR );
        if ( SYSTEM_MODULE_NAME.equals( moduleKey ) )
        {
            return ModuleKey.SYSTEM;
        }

        final String version = substringAfterLast( moduleKey, SEPARATOR );
        Preconditions.checkArgument( !isNullOrEmpty( version ), "Missing version in module key [" + moduleKey + "]" );

        final ModuleName moduleName = ModuleName.from( name );
        final ModuleVersion moduleVersion = ModuleVersion.from( version );
        return new ModuleKey( moduleName, moduleVersion );
    }

    public static ModuleKey from( final Bundle bundle )
    {
        final String name = bundle.getSymbolicName();
        final Version bundleVersion = bundle.getVersion();
        final ModuleVersion version = ModuleVersion.from( bundleVersion.toString() );
        return ModuleKey.from( ModuleName.from( name ), version );
    }
}
