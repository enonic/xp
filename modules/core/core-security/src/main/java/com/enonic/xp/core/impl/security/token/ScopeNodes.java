package com.enonic.xp.core.impl.security.token;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.token.Scope;

/**
 * How an id provider's scopes are stored:
 * <pre>
 * /identity/&lt;idp&gt;/
 *   scopes/                      {@value #SCOPES_TYPE_NAME}: a folder, next to users and groups
 *     &lt;scope id&gt;/             {@value #SCOPE_TYPE_NAME}: one scope, node name = scope id
 * </pre>
 * Lookups are by path or parent, so no property is indexed. Deleting the id provider deletes
 * everything.
 */
@NullMarked
final class ScopeNodes
{
    static final String SCOPES = "scopes";

    static final String SCOPES_TYPE_NAME = "scopes";

    static final String SCOPE_TYPE_NAME = "scope";

    static final NodeType SCOPES_TYPE = NodeType.from( SCOPES_TYPE_NAME );

    static final NodeType SCOPE_TYPE = NodeType.from( SCOPE_TYPE_NAME );

    static final IndexConfigDocument NO_INDEX = PatternIndexConfigDocument.create().defaultConfig( IndexConfig.NONE ).build();

    private ScopeNodes()
    {
    }

    static NodePath idProviderPath( final IdProviderKey idProvider )
    {
        return new NodePath( new NodePath( NodePath.ROOT, NodeName.from( PrincipalKey.IDENTITY_NODE_NAME ) ),
                             NodeName.from( idProvider.toString() ) );
    }

    static NodePath scopesPath( final IdProviderKey idProvider )
    {
        return new NodePath( idProviderPath( idProvider ), NodeName.from( SCOPES ) );
    }

    static NodePath scopePath( final IdProviderKey idProvider, final String id )
    {
        return new NodePath( scopesPath( idProvider ), NodeName.from( RegistryNodeNames.of( id ) ) );
    }

    /**
     * Whether the node is the scope's: of the type, and holding the id, since a node name spells an
     * id out but a lookup may spell another.
     */
    static boolean isScope( final Node node, final String id )
    {
        return SCOPE_TYPE.equals( node.getNodeType() ) && id.equals( node.data().getString( "id" ) );
    }

    static Scope fromNode( final IdProviderKey idProvider, final Node node )
    {
        return fromData( idProvider, node.data().getString( "id" ), node.data() );
    }

    static Scope fromData( final IdProviderKey idProvider, final String id, final PropertyTree tree )
    {
        final PropertySet data = tree.getRoot();
        final List<PrincipalKey> roles = new ArrayList<>();
        data.getStrings( "roles" ).forEach( role -> roles.add( PrincipalKey.from( role ) ) );
        return Scope.create()
            .idProvider( idProvider )
            .id( id )
            .title( data.getString( "title" ) )
            .description( data.getString( "description" ) )
            .roles( PrincipalKeys.from( roles ) )
            .required( Boolean.TRUE.equals( data.getBoolean( "required" ) ) )
            .build();
    }

    static PropertyTree toData( final Scope scope )
    {
        final PropertyTree tree = new PropertyTree();
        final PropertySet data = tree.getRoot();
        data.setString( "id", scope.getId() );
        data.setString( "title", scope.getTitle() );
        if ( scope.getDescription() != null )
        {
            data.setString( "description", scope.getDescription() );
        }
        data.addStrings( "roles", scope.getRoles().stream().map( PrincipalKey::toString ).toList() );
        data.setBoolean( "required", scope.isRequired() );
        return tree;
    }
}
