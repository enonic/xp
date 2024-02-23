package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.NodeId;


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

        final ApplyNodePermissionsParams applyNodePermissionsParams = ApplyNodePermissionsParams.create()
            .nodeId( nodeId )
            .permissions( params.getPermissions() )
            .overwriteChildPermissions( params.isOverwriteChildPermissions() )
            .applyPermissionsListener( params.getListener() )
            .build();

        final ApplyNodePermissionsResult result = nodeService.applyPermissions( applyNodePermissionsParams );

        return ApplyContentPermissionsResult.create()
            .setSucceedContents( ContentNodeHelper.translateNodePathsToContentPaths( result.getSucceedNodes().getPaths() ) )
            .setSkippedContents( ContentNodeHelper.translateNodePathsToContentPaths( result.getSkippedNodes().getPaths() ) )
            .build();
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
