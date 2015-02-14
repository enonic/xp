package com.enonic.xp.core.impl.content;

import com.enonic.wem.api.content.ApplyContentPermissionsParams;
import com.enonic.wem.api.node.ApplyNodePermissionsParams;
import com.enonic.wem.api.node.NodeId;


final class ApplyContentPermissionsCommand
    extends AbstractContentCommand
{
    private final ApplyContentPermissionsParams params;

    private ApplyContentPermissionsCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    int execute()
    {
        final NodeId nodeId = NodeId.from( params.getContentId().toString() );

        final ApplyNodePermissionsParams applyNodePermissionsParams = ApplyNodePermissionsParams.create().
            nodeId( nodeId ).
            overwriteChildPermissions( params.isOverwriteChildPermissions() ).
            modifier( params.getModifier() ).
            build();

        return nodeService.applyPermissions( applyNodePermissionsParams );
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
