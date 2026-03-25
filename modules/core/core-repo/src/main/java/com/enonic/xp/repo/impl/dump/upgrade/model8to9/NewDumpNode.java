package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.util.Set;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.NodeStoreVersion;

record NewDumpNode(String nodeId, String nodePath, NodeStoreVersion nodeVersion, Set<Branch> branches)
{
}
