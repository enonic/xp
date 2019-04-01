package com.enonic.xp.repo.impl.dump.upgrade;

import com.enonic.xp.dump.DumpUpgradeStepResult;
import com.enonic.xp.util.Version;

public class MissingModelVersionDumpUpgrader
    implements DumpUpgrader
{
    private static final Version MODEL_VERSION = new Version( 1 );

    private static final String NAME = "Initial model version";

    @Override
    public Version getModelVersion()
    {
        return MODEL_VERSION;
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public DumpUpgradeStepResult upgrade( final String dumpName )
    {
        //Nothing to upgrade except the meta data version
        return DumpUpgradeStepResult.create().
            initialVersion( Version.emptyVersion ).
            upgradedVersion( MODEL_VERSION ).
            stepName( NAME ).
            processed( 1 ).
            build();
    }
}
