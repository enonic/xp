package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.ContentIds;
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
        final NodeId nodeId = NodeId.from( params.getContentId().toString() );

        final ApplyNodePermissionsParams applyNodePermissionsParams = ApplyNodePermissionsParams.create().
            nodeId( nodeId ).
            permissions( params.getPermissions() ).
            inheritPermissions( params.isInheritPermissions() ).
            overwriteChildPermissions( params.isOverwriteChildPermissions() ).
            applyPermissionsListener( params.getListener() ).
            build();

        final ApplyNodePermissionsResult result = nodeService.applyPermissions( applyNodePermissionsParams );

        return ApplyContentPermissionsResult.create().
            setSucceedContents( ContentIds.from( result.getSucceedNodes().getIds().getAsStrings() ) ).
            setSkippedContents( ContentIds.from( result.getSkippedNodes().getIds().getAsStrings() ) ).
            build();
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
