package com.enonic.xp.repo.impl.dump.upgrade;

import com.enonic.xp.util.Version;

public interface DumpUpgrader
{
    Version getModelVersion();

    void upgrade( final String dumpName );

}
