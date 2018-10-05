package com.enonic.xp.repo.impl.dump.update;

import com.enonic.xp.dump.Version;

public interface DumpUpdater
{
    Version getModelVersion();

    void update( final String dumpName );

}
