package com.enonic.wem.repo.internal.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.branch.storage.BranchNodeVersion;
import com.enonic.wem.repo.internal.storage.StorageService;
import com.enonic.wem.repo.internal.version.NodeVersionDocumentId;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersion;

class CompareStatusResolver
{
    private final BranchNodeVersion source;

    private final BranchNodeVersion target;

    private final StorageService storageService;

    private CompareStatusResolver( Builder builder )
    {
        this.source = builder.source;
        this.target = builder.target;
        this.storageService = builder.storageService;
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

        final NodeState sourceState = source.getNodeState();
        final NodeState targetState = target.getNodeState();

        if ( sourceState.equals( NodeState.PENDING_DELETE ) && !targetState.equals( NodeState.PENDING_DELETE ) )
        {
            return CompareStatus.PENDING_DELETE;
        }
        else if ( !sourceState.equals( NodeState.PENDING_DELETE ) && targetState.equals( NodeState.PENDING_DELETE ) )
        {
            return CompareStatus.PENDING_DELETE_TARGET;
        }

        if ( !source.getNodePath().equals( target.getNodePath() ) )
        {
            return CompareStatus.MOVED;
        }

        return resolveFromVersion();
    }

    private CompareStatus resolveFromVersion()
    {
        final NodeVersion sourceVersion = getVersion( this.source );
        final NodeVersion targetVersion = getVersion( this.target );

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


    private NodeVersion getVersion( final BranchNodeVersion branchNodeVersion )
    {
        if ( branchNodeVersion == null )
        {
            throw new IllegalArgumentException( "Expected branchNodeVersion to be != null when trying to fetch NodeVersion" );
        }

        final NodeVersion version =
            storageService.getVersion( new NodeVersionDocumentId( branchNodeVersion.getNodeId(), branchNodeVersion.getVersionId() ),
                                       InternalContext.from( ContextAccessor.current() ) );

        if ( version == null )
        {
            throw new NodeNotFoundException(
                "Didn't find versionId '" + branchNodeVersion.getVersionId() + "' of Node with id '" + branchNodeVersion.getNodeId() +
                    "'" );
        }

        return version;
    }

    public static final class Builder
    {
        private BranchNodeVersion source;

        private BranchNodeVersion target;

        private StorageService storageService;

        private Builder()
        {
        }

        public Builder source( BranchNodeVersion source )
        {
            this.source = source;
            return this;
        }

        public Builder target( BranchNodeVersion target )
        {
            this.target = target;
            return this;
        }

        public Builder storageService( StorageService storageService )
        {
            this.storageService = storageService;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.storageService, "StorageService must be set" );
        }

        public CompareStatusResolver build()
        {
            this.validate();
            return new CompareStatusResolver( this );
        }
    }
}
