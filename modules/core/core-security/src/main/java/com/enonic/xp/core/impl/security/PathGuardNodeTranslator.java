package com.enonic.xp.core.impl.security;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.PathGuard;
import com.enonic.xp.security.UpdatePathGuardParams;
import com.enonic.xp.security.UserStoreAuthConfig;

public class PathGuardNodeTranslator
{
    private static final NodePath PARENT_NODE_PATH = NodePath.create( NodePath.ROOT ).
        addElement( "pathguard" ).
        build();

    static NodePath getPathGuardsNodePath()
    {
        return PARENT_NODE_PATH;
    }

    static NodeId getNodeId( final String key )
    {
        return NodeId.from( "pathguard:" + key );
    }

    static ImmutableList<PathGuard> fromNodes( final Nodes nodes )
    {
        ImmutableList.Builder<PathGuard> pathGuards = ImmutableList.builder();
        nodes.stream().
            map( PathGuardNodeTranslator::fromNode ).
            forEach( pathGuards::add );
        return pathGuards.build();
    }

    static PathGuard fromNode( final Node node )
    {
        final String key = node.name().toString();
        final PropertySet data = node.data().getRoot();
        final String displayName = data.getString( PathGuardPropertyPaths.DISPLAY_NAME_PATH );

        final PathGuard.Builder pathGuard = PathGuard.create().key( key ).displayName( displayName );

        if ( data.hasProperty( PathGuardPropertyPaths.AUTH_CONFIG_PATH ) )
        {
            final String applicationKey = data.getString( PathGuardPropertyPaths.AUTH_CONFIG_APPLICATION_PATH );
            final PropertySet config = data.getSet( PathGuardPropertyPaths.AUTH_CONFIG_FORM_PATH );
            final UserStoreAuthConfig authConfig = UserStoreAuthConfig.create().
                applicationKey( ApplicationKey.from( applicationKey ) ).
                config( config.toTree() ).
                build();
            pathGuard.authConfig( authConfig );
        }

        for ( String path : data.getStrings( PathGuardPropertyPaths.PATHS_PATH.toString() ) )
        {
            pathGuard.addPath( path );
        }

        return pathGuard.build();
    }

    public static CreateNodeParams toCreateNodeParams( final PathGuard pathGuard )
    {
        final CreateNodeParams.Builder builder = CreateNodeParams.create().
            setNodeId( getNodeId( pathGuard.getKey() ) ).
            name( pathGuard.getKey() ).
            parent( getPathGuardsNodePath() ).
            inheritPermissions( true ).
            indexConfigDocument( PrincipalIndexConfigFactory.create() );

        final PropertyTree data = new PropertyTree();
        data.setString( PathGuardPropertyPaths.DISPLAY_NAME_PATH, pathGuard.getDisplayName() );
        data.addStrings( PathGuardPropertyPaths.PATHS_PATH.toString(), pathGuard.getPaths() );

        final UserStoreAuthConfig authConfig = pathGuard.getAuthConfig();
        if ( authConfig != null )
        {
            data.setString( PathGuardPropertyPaths.AUTH_CONFIG_APPLICATION_PATH, authConfig.getApplicationKey().toString() );
            data.setSet( PathGuardPropertyPaths.AUTH_CONFIG_FORM_PATH, authConfig.getConfig().getRoot() );
        }

        return builder.data( data ).
            build();
    }

    static UpdateNodeParams toUpdateNodeParams( final UpdatePathGuardParams params, final NodeId nodeId )
    {
        final String displayName = params.getDisplayName();
        final UserStoreAuthConfig authConfig = params.getAuthConfig();
        return UpdateNodeParams.create().
            id( nodeId ).
            editor( editableNode -> {
                final PropertyTree nodeData = editableNode.data;
                nodeData.setString( PathGuardPropertyPaths.DISPLAY_NAME_PATH, displayName );
                if ( authConfig == null )
                {
                    if ( nodeData.hasProperty( PathGuardPropertyPaths.AUTH_CONFIG_PATH ) )
                    {
                        nodeData.removeProperty( PathGuardPropertyPaths.AUTH_CONFIG_PATH );
                    }
                }
                else
                {
                    nodeData.setString( PathGuardPropertyPaths.AUTH_CONFIG_APPLICATION_PATH, authConfig.getApplicationKey().toString() );
                    nodeData.setSet( PathGuardPropertyPaths.AUTH_CONFIG_FORM_PATH, authConfig.getConfig().getRoot() );
                }
            } ).
            build();
    }
}
