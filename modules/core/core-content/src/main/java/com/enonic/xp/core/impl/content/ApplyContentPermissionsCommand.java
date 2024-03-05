package com.enonic.xp.core.impl.content;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.NodeId;


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
            .overwriteChildPermissions( params.isOverwriteChildPermissions() ).applyPermissionsListener( params.getListener() );

        if ( params.isImmediate() )
        {
            applyNodePermissionsBuilder.addBranches( Branches.from( ContentConstants.BRANCH_MASTER ) );
        }

        final ApplyNodePermissionsResult result = nodeService.applyPermissions( applyNodePermissionsBuilder.build() );

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
