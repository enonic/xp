package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.repo.impl.InternalContext;
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

    public CompareStatus resolve()
    {
        if ( source == null && target == null )
        {
            throw new IllegalArgumentException( "Both source and target versions null" );
        }

        if ( source == null )
        {
            return CompareStatus.NEW_TARGET;
        }
        else if ( target == null )
        {
            return CompareStatus.NEW;
        }

        if ( source.equals( target ) )
        {
            return CompareStatus.EQUAL;
        }

        if ( !source.getNodePath().equals( target.getNodePath() ) )
        {
            return CompareStatus.MOVED;
        }

        return resolveFromVersion();
    }

    private CompareStatus resolveFromVersion()
    {
        final NodeVersionMetadata sourceVersion = getVersion( this.source );
        final NodeVersionMetadata targetVersion = getVersion( this.target );

        if ( sourceVersion.getTimestamp().isAfter( targetVersion.getTimestamp() ) )
        {
            return CompareStatus.NEWER;
        }

        if ( sourceVersion.getTimestamp().isBefore( targetVersion.getTimestamp() ) )
        {
            return CompareStatus.OLDER;
        }

        return CompareStatus.EQUAL;
    }


    private NodeVersionMetadata getVersion( final NodeBranchEntry nodeBranchEntry )
    {
        if ( nodeBranchEntry == null )
        {
            throw new IllegalArgumentException( "Expected branchNodeVersion to be != null when trying to fetch NodeVersion" );
        }

        final NodeVersionMetadata version = nodeStorageService.getVersion( nodeBranchEntry.getNodeId(), nodeBranchEntry.getVersionId(),
                                                                           InternalContext.from( ContextAccessor.current() ) );

        if ( version == null )
        {
            throw new NodeNotFoundException(
                "Didn't find versionId '" + nodeBranchEntry.getVersionId() + "' of Node with id '" + nodeBranchEntry.getNodeId() + "'" );
        }

        return version;
    }

    public static final class Builder
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
            Preconditions.checkNotNull( this.nodeStorageService, "StorageService must be set" );
        }

        public CompareStatusResolver build()
        {
            this.validate();
            return new CompareStatusResolver( this );
        }
    }
}
