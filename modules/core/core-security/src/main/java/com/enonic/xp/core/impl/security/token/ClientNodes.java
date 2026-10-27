package com.enonic.xp.core.impl.security.token;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
import com.enonic.xp.security.token.Client;

/**
 * How an id provider's clients are stored:
 * <pre>
 * /identity/&lt;idp&gt;/
 *   clients/                     {@value #CLIENTS_TYPE_NAME}: a folder, next to users and groups
 *     &lt;client id&gt;/            {@value #CLIENT_TYPE_NAME}: one client, node name = client id
 * </pre>
 * Lookups are by path or parent, so no property is indexed. Deleting the id provider deletes
 * everything.
 */
@NullMarked
final class ClientNodes
{
    static final String CLIENTS = "clients";

    static final String CLIENTS_TYPE_NAME = "clients";

    static final String CLIENT_TYPE_NAME = "client";

    static final NodeType CLIENTS_TYPE = NodeType.from( CLIENTS_TYPE_NAME );

    static final NodeType CLIENT_TYPE = NodeType.from( CLIENT_TYPE_NAME );

    static final IndexConfigDocument NO_INDEX = PatternIndexConfigDocument.create().defaultConfig( IndexConfig.NONE ).build();

    private ClientNodes()
    {
    }

    static NodePath clientsPath( final IdProviderKey idProvider )
    {
        return new NodePath( ScopeNodes.idProviderPath( idProvider ), NodeName.from( CLIENTS ) );
    }

    static NodePath clientPath( final IdProviderKey idProvider, final String id )
    {
        return new NodePath( clientsPath( idProvider ), NodeName.from( RegistryNodeNames.of( id ) ) );
    }

    /**
     * Whether the node is the client's: of the type, and holding the id, since a node name spells
     * an id out but a lookup may spell another.
     */
    static boolean isClient( final Node node, final String id )
    {
        return CLIENT_TYPE.equals( node.getNodeType() ) && id.equals( node.data().getString( "id" ) );
    }

    static Client fromNode( final IdProviderKey idProvider, final Node node )
    {
        return fromData( idProvider, node.data().getString( "id" ), node.data() );
    }

    static Client fromData( final IdProviderKey idProvider, final String id, final PropertyTree tree )
    {
        final PropertySet data = tree.getRoot();
        final String grantTtl = data.getString( "grantTtl" );
        return Client.create()
            .idProvider( idProvider )
            .id( id )
            .displayName( data.getString( "displayName" ) )
            .uri( data.getString( "uri" ) )
            .logoUri( data.getString( "logoUri" ) )
            .policyUri( data.getString( "policyUri" ) )
            .tosUri( data.getString( "tosUri" ) )
            .grantTypes( strings( data, "grantTypes" ) )
            .redirectUris( strings( data, "redirectUris" ) )
            .scope( strings( data, "scope" ) )
            .resource( strings( data, "resource" ) )
            .grantTtl( grantTtl == null ? null : Duration.parse( grantTtl ) )
            .enabled( Boolean.TRUE.equals( data.getBoolean( "enabled" ) ) )
            .build();
    }

    static PropertyTree toData( final Client client )
    {
        final PropertyTree tree = new PropertyTree();
        final PropertySet data = tree.getRoot();
        data.setString( "id", client.getId() );
        data.setString( "displayName", client.getDisplayName() );
        setIfPresent( data, "uri", client.getUri() );
        setIfPresent( data, "logoUri", client.getLogoUri() );
        setIfPresent( data, "policyUri", client.getPolicyUri() );
        setIfPresent( data, "tosUri", client.getTosUri() );
        data.addStrings( "grantTypes", client.getGrantTypes() );
        data.addStrings( "redirectUris", client.getRedirectUris() );
        data.addStrings( "scope", client.getScope() );
        data.addStrings( "resource", client.getResource() );
        if ( client.getGrantTtl() != null )
        {
            data.setString( "grantTtl", client.getGrantTtl().toString() );
        }
        data.setBoolean( "enabled", client.isEnabled() );
        return tree;
    }

    private static void setIfPresent( final PropertySet data, final String name, final @Nullable String value )
    {
        if ( value != null )
        {
            data.setString( name, value );
        }
    }

    private static List<String> strings( final PropertySet set, final String name )
    {
        final List<String> values = new ArrayList<>();
        set.getStrings( name ).forEach( values::add );
        return values;
    }
}
