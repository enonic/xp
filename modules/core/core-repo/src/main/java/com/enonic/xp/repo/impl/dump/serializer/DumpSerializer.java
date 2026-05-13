package com.enonic.xp.repo.impl.dump.serializer;

import java.util.Collection;
import java.util.List;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;

public interface DumpSerializer
{
    /**
     * Serialize one version of one node — a single JSONL line (no trailing newline).
     * When {@code branches} is non-empty the line carries them, marking this version as the
     * active head in those branches.
     */
    byte[] serialize( String nodeId, VersionMeta version, Collection<Branch> branches );

    byte[] serialize( CommitDumpEntry commitDumpEntry );

    /**
     * Parse a single JSONL line into ({@code nodeId}, {@link VersionMeta}, branches).
     */
    NodeVersionLine toNodeVersionLine( String value );

    CommitDumpEntry toCommitDumpEntry( String value );

    record NodeVersionLine(String nodeId, VersionMeta version, List<String> branches)
    {
    }
}
