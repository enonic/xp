package com.enonic.xp.repo.impl.dump.model;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.core.internal.Millis;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.security.PrincipalKey;

public record CommitDumpEntry(NodeCommitId nodeCommitId, String message, Instant timestamp, PrincipalKey committer)
{
    public CommitDumpEntry
    {
        message = Objects.requireNonNullElse( message, "" );
        timestamp = Millis.from( timestamp );
        committer = committer == null ? PrincipalKey.ofAnonymous() : committer;
    }
}
