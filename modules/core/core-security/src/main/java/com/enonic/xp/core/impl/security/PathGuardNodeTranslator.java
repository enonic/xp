package com.enonic.xp.core.impl.security;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.PathGuard;
import com.enonic.xp.security.PathGuardKey;
import com.enonic.xp.security.UpdatePathGuardParams;
import com.enonic.xp.security.UserStoreKey;

public class PathGuardNodeTranslator
{
    private static final NodePath PARENT_NODE_PATH = NodePath.create( NodePath.ROOT ).
        addElement( "pathguard" ).
        build();

    static NodePath getPathGuardsNodePath()
    {
        return PARENT_NODE_PATH;
    }

    static NodeId getNodeId( final PathGuardKey key )
    {
        return NodeId.from( "pathguard:" + key.toString() );
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
        final PathGuardKey key = PathGuardKey.from( node.name().toString() );
        final PropertySet data = node.data().getRoot();
        final String displayName = data.getString( PathGuardPropertyPaths.DISPLAY_NAME_PATH );
        final String description = data.getString( PathGuardPropertyPaths.DESCRIPTION_PATH );
        final String userStoreKey = data.getString( PathGuardPropertyPaths.USER_STORE_KEY_PATH );
        final Boolean passive = data.getBoolean( PathGuardPropertyPaths.PASSIVE_PATH );

        final PathGuard.Builder pathGuard = PathGuard.create().
            key( key ).
            displayName( displayName ).
            description( description ).
            userStoreKey( userStoreKey == null ? null : UserStoreKey.from( userStoreKey ) ).
            passive( passive == null ? false : passive.booleanValue() );

        for ( String path : data.getStrings( PathGuardPropertyPaths.PATHS_PATH.toString() ) )
        {
            pathGuard.addPath( path );
        }

        return pathGuard.build();
    }

    public static CreateNodeParams toCreateNodeParams( final PathGuard pathGuard )
    {
        final String userStoreKey = pathGuard.getUserStoreKey() == null ? null : pathGuard.getUserStoreKey().toString();
        final PropertyTree data = new PropertyTree();
        data.setString( PathGuardPropertyPaths.DISPLAY_NAME_PATH, pathGuard.getDisplayName() );
        data.setString( PathGuardPropertyPaths.DESCRIPTION_PATH, pathGuard.getDescription() );
        data.setString( PathGuardPropertyPaths.USER_STORE_KEY_PATH, userStoreKey );
        data.setBoolean( PathGuardPropertyPaths.PASSIVE_PATH, pathGuard.isPassive() );
        data.addStrings( PathGuardPropertyPaths.PATHS_PATH.toString(), pathGuard.getPaths() );

        return CreateNodeParams.create().
            setNodeId( getNodeId( pathGuard.getKey() ) ).
            name( pathGuard.getKey().toString() ).
            parent( getPathGuardsNodePath() ).
            inheritPermissions( true ).
            indexConfigDocument( PrincipalIndexConfigFactory.create() ).
            data( data ).
            build();
    }

    static UpdateNodeParams toUpdateNodeParams( final UpdatePathGuardParams params, final NodeId nodeId )
    {
        return UpdateNodeParams.create().
            id( nodeId ).
            editor( editableNode -> {
                final PropertyTree data = editableNode.data;
                data.setString( PathGuardPropertyPaths.DISPLAY_NAME_PATH, params.getDisplayName() );
                data.setString( PathGuardPropertyPaths.DESCRIPTION_PATH, params.getDescription() );
                data.setString( PathGuardPropertyPaths.USER_STORE_KEY_PATH, params.getUserStoreKey().toString() );
                data.setBoolean( PathGuardPropertyPaths.PASSIVE_PATH, params.isPassive() );
                data.removeProperty( PathGuardPropertyPaths.PATHS_PATH );
                data.addStrings( PathGuardPropertyPaths.PATHS_PATH.toString(), params.getPaths() );
            } ).
            build();
    }
}
