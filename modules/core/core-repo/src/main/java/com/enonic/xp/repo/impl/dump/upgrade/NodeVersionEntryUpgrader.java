package com.enonic.xp.repo.impl.dump.upgrade;

import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;

public interface NodeVersionEntryUpgrader
{
    VersionDumpEntryJson upgradeVersionEntry( VersionDumpEntryJson versionEntry );
}
