package com.enonic.xp.repo.impl.dump.update;

import com.enonic.xp.util.Version;

public class MissingModelVersionDumpUpdater
    implements DumpUpdater
{
    @Override
    public Version getModelVersion()
    {
        return new Version( 1, 0, 0 );
    }

    @Override
    public void update( final String dumpName )
    {
    }
}
