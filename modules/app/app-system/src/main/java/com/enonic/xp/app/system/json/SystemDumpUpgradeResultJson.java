package com.enonic.xp.app.system.json;

import java.util.List;

import com.enonic.xp.dump.DumpUpgradeResult;

public record SystemDumpUpgradeResultJson(String initialVersion, String upgradedVersion, List<SystemDumpUpgradeStepResultJson> stepResults)
{
    public static SystemDumpUpgradeResultJson from( final DumpUpgradeResult result )
    {
        return new SystemDumpUpgradeResultJson( result.getInitialVersion().toString(), result.getUpgradedVersion().toString(),
                                                result.getStepResults().stream().map( SystemDumpUpgradeStepResultJson::from ).toList() );
    }

    @Override
    public String toString()
    {
        return JsonHelper.toJson( this );
    }
}
