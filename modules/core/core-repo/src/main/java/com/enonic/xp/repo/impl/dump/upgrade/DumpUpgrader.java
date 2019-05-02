package com.enonic.xp.repo.impl.dump.upgrade;

import com.enonic.xp.dump.DumpUpgradeStepResult;
import com.enonic.xp.util.Version;

public interface DumpUpgrader
{
    Version getModelVersion();

    String getName();

    DumpUpgradeStepResult upgrade( final String dumpName );
}
