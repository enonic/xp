package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.Set;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.dump.serializer.json.BranchDumpEntryJson;

record AdditionalDumpEntry(BranchDumpEntryJson branchEntry, Set<Branch> branches)
{
}
