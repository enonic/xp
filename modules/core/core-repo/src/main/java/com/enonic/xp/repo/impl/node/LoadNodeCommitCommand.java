package com.enonic.xp.repo.impl.node;

import java.time.Instant;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.StoreNodeCommitParams;
import com.enonic.xp.security.PrincipalKey;

public class LoadNodeCommitCommand
    extends AbstractNodeCommand
{
    private final NodeCommitId nodeCommitId;

    private final String message;

    private final PrincipalKey committer;

    private final Instant timestamp;

    private LoadNodeCommitCommand( final Builder builder )
    {
        super( builder );
        nodeCommitId = builder.nodeCommitId;
        message = builder.message;
        committer = builder.committer;
        timestamp = builder.timestamp;
    }

    public void execute()
    {
        this.nodeStorageService.storeCommit( StoreNodeCommitParams.create().
            nodeCommitId( this.nodeCommitId ).
            message( this.message ).
            committer( this.committer ).
            timestamp( this.timestamp ).
            build(), InternalContext.from( ContextAccessor.current() ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeCommitId nodeCommitId;

        private String message;

        private PrincipalKey committer;

        private Instant timestamp;

        private Builder()
        {
        }

        public LoadNodeCommitCommand build()
        {
            return new LoadNodeCommitCommand( this );
        }

        public Builder nodeCommitId( final NodeCommitId val )
        {
            nodeCommitId = val;
            return this;
        }

        public Builder message( final String val )
        {
            message = val;
            return this;
        }

        public Builder committer( final PrincipalKey val )
        {
            committer = val;
            return this;
        }

        public Builder timestamp( final Instant val )
        {
            timestamp = val;
            return this;
        }
    }
}
