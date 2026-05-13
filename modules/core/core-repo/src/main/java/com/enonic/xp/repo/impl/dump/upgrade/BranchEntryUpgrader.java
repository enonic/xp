package com.enonic.xp.repo.impl.dump.upgrade;

import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;

public interface BranchEntryUpgrader
{
    VersionDumpEntryJson upgradeBranchMeta( NodeStoreVersion nodeVersion, VersionDumpEntryJson meta );
}
