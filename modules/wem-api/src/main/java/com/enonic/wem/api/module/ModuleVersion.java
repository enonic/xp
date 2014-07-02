package com.enonic.wem.api.module;

import com.enonic.wem.api.BaseVersion;

public final class ModuleVersion
    extends BaseVersion
    implements Comparable<ModuleVersion>
{
    public ModuleVersion( final String version )
    {
        super( version );
    }

    @Override
    public int compareTo( final ModuleVersion other )
    {
        return super.compareTo( other );
    }

    public static ModuleVersion from( final String version )
    {
        return new ModuleVersion( version );
    }
}
