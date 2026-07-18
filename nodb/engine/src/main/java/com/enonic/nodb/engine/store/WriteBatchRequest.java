package com.enonic.nodb.engine.store;

import java.util.List;

import com.enonic.nodb.engine.model.BranchEntryRecord;
import com.enonic.nodb.engine.model.CommitRecord;
import com.enonic.nodb.engine.model.RepoRef;
import com.enonic.nodb.engine.model.VersionRecord;

/**
 * One atomic write: N versions + N branch entries + an optional commit + the payloads
 * they reference, executed as a single transaction by {@link WriteService#write}.
 */
public record WriteBatchRequest(RepoRef repo, List<PayloadRef> payloads, List<VersionRecord> versions,
                                 List<BranchEntryRecord> branchEntries, CommitRecord commit)
{
}
