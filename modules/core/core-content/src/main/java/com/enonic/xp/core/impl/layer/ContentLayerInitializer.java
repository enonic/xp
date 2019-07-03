package com.enonic.xp.core.impl.layer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.init.ExternalInitializer;
import com.enonic.xp.layer.ContentLayerConstants;
import com.enonic.xp.layer.ContentLayerName;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class ContentLayerInitializer
    extends ExternalInitializer
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentLayerInitializer.class );

    private final NodeService nodeService;

    private ContentLayerInitializer( final Builder builder )
    {
        super( builder );
        nodeService = builder.nodeService;
    }

    @Override
    protected boolean isInitialized()
    {
        return createAdminContext().
            callWith( () -> this.nodeService.getByPath( ContentLayerConstants.DEFAULT_LAYER_PATH ) != null );
    }

    @Override
    protected void doInitialize()
    {
        createAdminContext().runWith( () -> {
            initializeLayerParent();
            initializeDefaultLayer();
        } );
    }

    private void initializeLayerParent()
    {
        if ( !nodeService.nodeExists( ContentLayerConstants.LAYER_PARENT_PATH ) )
        {
            LOG.info( "Initializing [" + ContentLayerConstants.LAYER_PARENT_PATH.toString() + "] folder" );

            nodeService.create( CreateNodeParams.create().
                parent( ContentLayerConstants.LAYER_PARENT_PATH.getParentPath() ).
                name( ContentLayerConstants.LAYER_PARENT_PATH.getLastElement().toString() ).
                inheritPermissions( true ).
                build() );
        }
    }

    private void initializeDefaultLayer()
    {
        if ( !nodeService.nodeExists( ContentLayerConstants.DEFAULT_LAYER_PATH ) )
        {
            LOG.info( "Initializing [" + ContentLayerConstants.DEFAULT_LAYER_PATH.toString() + "] layer" );

            PropertyTree data = new PropertyTree();
            data.setString( "name", ContentLayerName.DEFAULT_LAYER_NAME.getValue() );
            data.setString( "displayName", "Default Layer" );

            nodeService.create( CreateNodeParams.create().
                parent( ContentLayerConstants.DEFAULT_LAYER_PATH.getParentPath() ).
                name( ContentLayerConstants.DEFAULT_LAYER_PATH.getLastElement().toString() ).
                data( data ).
                nodeType( NodeType.from( ContentLayerConstants.NODE_TYPE ) ).
                inheritPermissions( true ).
                build() );
        }
    }

    @Override
    protected String getInitializationSubject()
    {
        return "com.enonic.cms.default [layers] layout";
    }

    private Context createAdminContext()
    {
        final User admin = User.create().
            key( PrincipalKey.ofSuperUser() ).
            login( PrincipalKey.ofSuperUser().getId() ).
            build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().
            principals( RoleKeys.ADMIN ).
            user( admin ).
            build();
        return ContextBuilder.from( ContentConstants.CONTEXT_MASTER ).
            authInfo( authInfo ).
            build();
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
        extends ExternalInitializer.Builder<Builder>
    {
        private NodeService nodeService;

        private Builder()
        {
        }

        public Builder setNodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public ContentLayerInitializer build()
        {
            return new ContentLayerInitializer( this );
        }
    }
}
