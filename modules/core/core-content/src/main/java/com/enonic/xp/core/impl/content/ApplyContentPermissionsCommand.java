package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.ApplyPermissionsListener;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.ApplyNodePermissionsListener;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.ApplyPermissionsScope;
import com.enonic.xp.node.CommitNodeParams;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionIds;


final class ApplyContentPermissionsCommand
    extends AbstractContentCommand
{
    private final ApplyContentPermissionsParams params;

    private ApplyContentPermissionsCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    ApplyContentPermissionsResult execute()
    {
        final NodeId nodeId = NodeId.from( params.getContentId() );

        final ApplyNodePermissionsParams.Builder applyNodePermissionsBuilder = ApplyNodePermissionsParams.create()
            .nodeId( nodeId )
            .permissions( params.getPermissions() )
            .addPermissions( params.getAddPermissions() )
            .removePermissions( params.getRemovePermissions() )
            .scope( Enum.valueOf( ApplyPermissionsScope.class, params.getScope().name() ) )
            .addBranches( Branches.from( ContentConstants.BRANCH_MASTER, ContentConstants.BRANCH_DRAFT ) );

        if ( params.getListener() != null )
        {
            applyNodePermissionsBuilder.applyPermissionsListener( new ListenerDelegate( params.getListener() ) );
        }

        applyNodePermissionsBuilder.versionAttributes(
            ContentAttributesHelper.versionHistoryAttr( ContentAttributesHelper.PERMISSIONS_KEY ) );

        final ApplyNodePermissionsResult result = nodeService.applyPermissions( applyNodePermissionsBuilder.build() );

        commitResult( result );

        final ApplyContentPermissionsResult.Builder builder = ApplyContentPermissionsResult.create();

        result.getResults().forEach( ( id, branchResults ) -> {
            for ( ApplyNodePermissionsResult.BranchResult branchResult : branchResults )
            {
                if ( branchResult.branch().equals( ContextAccessor.current().getBranch() ) )
                {
                    builder.addResult( ContentId.from( id ), branchResult.permissions() );
                    break;
                }
            }
        } );

        return builder.build();
    }

    private void commitResult( final ApplyNodePermissionsResult result )
    {
        final NodeVersionIds versionIdsToCommit = result.getResults()
            .entrySet()
            .stream()
            .flatMap( entry -> entry.getValue()
                .stream()
                .filter( br -> ContentConstants.BRANCH_MASTER.equals( br.branch() ) )
                .map( ApplyNodePermissionsResult.BranchResult::nodeVersionId )
                .filter( Objects::nonNull ) )
            .collect( NodeVersionIds.collector() );

        if ( !versionIdsToCommit.isEmpty() )
        {
            nodeService.commit( CommitNodeParams.create()
                                    .nodeCommitEntry(
                                        NodeCommitEntry.create().message( ContentConstants.APPLY_PERMISSIONS_COMMIT_PREFIX ).build() )
                                    .nodeVersionIds( versionIdsToCommit )
                                    .build() );
        }
    }

    public static Builder create( final ApplyContentPermissionsParams params )
    {
        return new Builder( params );
    }

    static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private final ApplyContentPermissionsParams params;

        private Builder( final ApplyContentPermissionsParams params )
        {
            this.params = params;
        }

        @Override
        void validate()
        {
            super.validate();
        }

        ApplyContentPermissionsCommand build()
        {
            validate();
            return new ApplyContentPermissionsCommand( this );
        }
    }

    private static final class ListenerDelegate
        implements ApplyNodePermissionsListener
    {
        ApplyPermissionsListener delegate;

        ListenerDelegate( ApplyPermissionsListener delegate )
        {
            this.delegate = delegate;
        }

        @Override
        public void setTotal( final int count )
        {
            delegate.setTotal( count );
        }

        @Override
        public void permissionsApplied( final int count )
        {
            delegate.permissionsApplied( count );
        }

        @Override
        public void notEnoughRights( final int count )
        {
            delegate.notEnoughRights( count );
        }
    }
}
