package com.enonic.xp.app.system.json;

import com.enonic.xp.dump.DumpUpgradeStepResult;

public record SystemDumpUpgradeStepResultJson(String initialVersion, String upgradedVersion, String stepName, long processed, long errors,
                                              long warnings)
{
    public static SystemDumpUpgradeStepResultJson from( final DumpUpgradeStepResult result )
    {
        return new SystemDumpUpgradeStepResultJson( result.getInitialVersion().toShortestString(),
                                                    result.getUpgradedVersion().toShortestString(), result.getStepName(),
                                                    result.getProcessed(), result.getErrors(), result.getWarnings() );
    }
}
