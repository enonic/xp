package com.enonic.xp.repo.impl.dump.upgrade;

import com.enonic.xp.dump.DumpUpgradeStepResult;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.util.Version;

public interface DumpUpgrader
{
    Version getModelVersion();

    default String getName()
    {
        return getClass().getSimpleName();
    }

    DumpUpgradeStepResult upgrade( DumpWriter dumpWriter );
}
