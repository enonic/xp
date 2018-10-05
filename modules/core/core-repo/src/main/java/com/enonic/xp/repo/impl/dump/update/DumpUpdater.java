package com.enonic.xp.repo.impl.dump.update;

import com.enonic.xp.util.Version;

public interface DumpUpdater
{
    Version getModelVersion();

    void update( final String dumpName );

}
