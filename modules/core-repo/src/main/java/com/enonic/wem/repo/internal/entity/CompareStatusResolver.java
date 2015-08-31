package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.repo.internal.storage.branch.NodeBranchVersion;
import com.enonic.wem.repo.internal.version.VersionService;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repository.RepositoryId;

class CompareStatusResolver
{
    private final NodeBranchVersion source;

    private final NodeBranchVersion target;

    private final VersionService versionService;

    private final RepositoryId repositoryId;

    private CompareStatusResolver( Builder builder )
    {
        this.source = builder.source;
        this.target = builder.target;
        this.versionService = builder.versionService;
        this.repositoryId = builder.repositoryId;
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
        final NodeVersionId sourceVersionId = this.source.getVersionId();
        final NodeVersionId targetVersionId = this.target.getVersionId();

        final NodeVersion sourceVersion = getVersion( sourceVersionId );
        final NodeVersion targetVersion = getVersion( targetVersionId );

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


    private NodeVersion getVersion( final NodeVersionId nodeVersionId )
    {
        if ( nodeVersionId == null )
        {
            return null;
        }

        return versionService.getVersion( nodeVersionId, this.repositoryId );
    }


    public static final class Builder
    {
        private NodeBranchVersion source;

        private NodeBranchVersion target;

        private VersionService versionService;

        private RepositoryId repositoryId;

        private Builder()
        {
        }

        public Builder source( NodeBranchVersion source )
        {
            this.source = source;
            return this;
        }

        public Builder target( NodeBranchVersion target )
        {
            this.target = target;
            return this;
        }

        public Builder versionService( VersionService versionService )
        {
            this.versionService = versionService;
            return this;
        }

        public Builder repositoryId( RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public CompareStatusResolver build()
        {
            return new CompareStatusResolver( this );
        }
    }
}
