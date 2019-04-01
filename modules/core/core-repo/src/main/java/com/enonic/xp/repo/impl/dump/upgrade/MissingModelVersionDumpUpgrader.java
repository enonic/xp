package com.enonic.xp.repo.impl.dump.upgrade;

import com.enonic.xp.dump.DumpUpgradeStepResult;
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
