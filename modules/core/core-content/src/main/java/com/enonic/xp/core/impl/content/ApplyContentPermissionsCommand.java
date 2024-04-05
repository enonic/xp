package com.enonic.xp.core.impl.content;

import java.util.Collection;
import java.util.Objects;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.RoutableNodeVersionId;
import com.enonic.xp.node.RoutableNodeVersionIds;


final class ApplyContentPermissionsCommand
    extends AbstractContentCommand
{
    private final ApplyContentPermissionsParams params;

    private final ContentNodeTranslator contentNodeTranslator;

    private ApplyContentPermissionsCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.contentNodeTranslator = new ContentNodeTranslator( this.nodeService );
    }

    ApplyContentPermissionsResult execute()
    {
        final NodeId nodeId = NodeId.from( params.getContentId() );

        final ApplyNodePermissionsParams.Builder applyNodePermissionsBuilder = ApplyNodePermissionsParams.create()
            .nodeId( nodeId )
            .permissions( params.getPermissions() )
            .addPermissions( params.getAddPermissions() )
            .removePermissions( params.getRemovePermissions() )
            .overwriteChildPermissions( params.isOverwriteChildPermissions() )
            .applyPermissionsListener( params.getListener() )
            .addBranches( Branches.from( ContentConstants.BRANCH_MASTER ) );

        final ApplyNodePermissionsResult result = nodeService.applyPermissions( applyNodePermissionsBuilder.build() );

        commitResult( result );

        final ApplyContentPermissionsResult.Builder builder = ApplyContentPermissionsResult.create();

        result.getBranchResults().forEach( ( id, branchResult ) -> {
            branchResult.forEach( br -> builder.addBranchResult( ContentId.from( id ), br.getBranch(),
                                                                 br.getNode() != null
                                                                     ? ContextBuilder.from( ContextAccessor.current() )
                                                                     .branch( br.getBranch() )
                                                                     .build()
                                                                     .callWith( () -> contentNodeTranslator.fromNode( br.getNode(), true ) )
                                                                     : null ) );
        } );

        return builder.build();
    }

    private void commitResult( final ApplyNodePermissionsResult result )
    {
        final RoutableNodeVersionIds versionIdsToCommit = result.getBranchResults()
            .values()
            .stream()
            .flatMap( Collection::stream )
            .filter( branchResult -> ContentConstants.BRANCH_MASTER.equals( branchResult.getBranch() ) )
            .map( ApplyNodePermissionsResult.BranchResult::getNode )
            .filter( Objects::nonNull )
            .map( node -> RoutableNodeVersionId.from( node.id(), node.getNodeVersionId() ) )
            .collect( RoutableNodeVersionIds.collecting() );

        if ( !versionIdsToCommit.isEmpty() )
        {
            nodeService.commit( NodeCommitEntry.create().message( ContentConstants.APPLY_PERMISSIONS_COMMIT_PREFIX )
                                    .build(), versionIdsToCommit );
        }
    }

    public static Builder create( final ApplyContentPermissionsParams params )
    {
        return new Builder( params );
    }

    public static class Builder
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

        public ApplyContentPermissionsCommand build()
        {
            validate();
            return new ApplyContentPermissionsCommand( this );
        }
    }

}
