package com.enonic.xp.repo.impl.node;

import java.util.Objects;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeCompareStatus;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.storage.NodeStorageService;

class CompareStatusResolver
{
    private final NodeBranchEntry source;

    private final NodeBranchEntry target;

    private final NodeStorageService nodeStorageService;

    private CompareStatusResolver( Builder builder )
    {
        this.source = builder.source;
        this.target = builder.target;
        this.nodeStorageService = builder.nodeStorageService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeComparison resolve()
    {
        if ( source == null && target == null )
        {
            throw new IllegalArgumentException( "Both source and target versions null" );
        }

        if ( source == null )
        {
            return new NodeComparison( null, null, target.getNodeId(), target.getNodePath(), NodeCompareStatus.NEW_TARGET );
        }
        else if ( target == null )
        {
            return new NodeComparison( source.getNodeId(), source.getNodePath(), null, null, NodeCompareStatus.NEW );
        }

        if ( source.equals( target ) )
        {
            return new NodeComparison( source.getNodeId(), source.getNodePath(), target.getNodeId(), target.getNodePath(),
                                       NodeCompareStatus.EQUAL );
        }

        if ( !source.getNodePath().equals( target.getNodePath() ) )
        {
            return new NodeComparison( source.getNodeId(), source.getNodePath(), target.getNodeId(), target.getNodePath(),
                                       NodeCompareStatus.MOVED );
        }

        return new NodeComparison( source.getNodeId(), source.getNodePath(), target.getNodeId(), target.getNodePath(),
                                   resolveFromVersion() );
    }

    private NodeCompareStatus resolveFromVersion()
    {
        final NodeVersionMetadata sourceVersion = getVersion( this.source );
        final NodeVersionMetadata targetVersion = getVersion( this.target );

        if ( sourceVersion.getTimestamp().isAfter( targetVersion.getTimestamp() ) )
        {
            return NodeCompareStatus.NEWER;
        }

        if ( sourceVersion.getTimestamp().isBefore( targetVersion.getTimestamp() ) )
        {
            return NodeCompareStatus.OLDER;
        }

        return NodeCompareStatus.EQUAL;
    }

    private NodeVersionMetadata getVersion( final NodeBranchEntry nodeBranchEntry )
    {
        final NodeVersionMetadata version = nodeStorageService.getVersion( nodeBranchEntry.getVersionId(),
                                                                           InternalContext.from( ContextAccessor.current() ) );

        if ( version == null )
        {
            throw new NodeNotFoundException(
                "Didn't find versionId '" + nodeBranchEntry.getVersionId() + "' of Node with id '" + nodeBranchEntry.getNodeId() + "'" );
        }

        return version;
    }

    static final class Builder
    {
        private NodeBranchEntry source;

        private NodeBranchEntry target;

        private NodeStorageService nodeStorageService;

        private Builder()
        {
        }

        public Builder source( NodeBranchEntry source )
        {
            this.source = source;
            return this;
        }

        public Builder target( NodeBranchEntry target )
        {
            this.target = target;
            return this;
        }

        public Builder storageService( NodeStorageService nodeStorageService )
        {
            this.nodeStorageService = nodeStorageService;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( this.nodeStorageService );
        }

        CompareStatusResolver build()
        {
            this.validate();
            return new CompareStatusResolver( this );
        }
    }
}
