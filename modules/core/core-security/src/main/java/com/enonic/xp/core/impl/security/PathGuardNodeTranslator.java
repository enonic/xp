package com.enonic.xp.core.impl.security;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.security.PathGuard;
import com.enonic.xp.security.UserStoreAuthConfig;

public class PathGuardNodeTranslator
{
    private static final NodePath PARENT_NODE_PATH = NodePath.create( NodePath.ROOT ).
        addElement( "guard" ).
        build();

    static NodePath getPathGuardsNodePath()
    {
        return PARENT_NODE_PATH;
    }

    static ImmutableList<PathGuard> fromNodes( final Nodes nodes )
    {
        ImmutableList.Builder<PathGuard> pathGuards = ImmutableList.builder();
        nodes.stream().
            map( PathGuardNodeTranslator::fromNode ).
            forEach( pathGuards::add );
        return pathGuards.build();
    }

    private static PathGuard fromNode( final Node node )
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

}
