package com.enonic.xp.repo.impl.dump.upgrade;

import com.enonic.xp.util.Version;

public class MissingModelVersionDumpUpgrader
    implements DumpUpgrader
{
    @Override
    public Version getModelVersion()
    {
        return new Version( 1, 0, 0 );
    }

    @Override
    public void upgrade( final String dumpName )
    {
        //Nothing to upgrade except the meta data version
    }
}
