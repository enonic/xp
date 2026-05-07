package com.enonic.xp.repo.impl.dump.model;

import java.time.Instant;
import com.enonic.xp.core.internal.Millis;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.security.PrincipalKey;

import static java.util.Objects.requireNonNullElse;

public record CommitDumpEntry(NodeCommitId nodeCommitId, String message, Instant timestamp, PrincipalKey committer)
{
    public CommitDumpEntry
    {
        message = requireNonNullElse( message, "" );
        timestamp = Millis.from( timestamp );
        committer = committer == null ? PrincipalKey.ofAnonymous() : committer;
    }
}
