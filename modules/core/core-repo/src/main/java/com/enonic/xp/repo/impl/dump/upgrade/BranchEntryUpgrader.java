package com.enonic.xp.repo.impl.dump.upgrade;

import com.enonic.xp.repo.impl.dump.serializer.json.BranchDumpEntryJson;

public interface BranchEntryUpgrader
{
    BranchDumpEntryJson upgradeBranchEntry( BranchDumpEntryJson branchEntry );
}
