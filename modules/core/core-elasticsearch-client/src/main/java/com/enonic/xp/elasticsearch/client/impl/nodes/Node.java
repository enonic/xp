package com.enonic.xp.elasticsearch.client.impl.nodes;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.XContentParser;

public class Node
{
    private static final ParseField NAME_FILED = new ParseField( "name" );

    private static final ParseField VERSION_FILED = new ParseField( "version" );

    private static final ParseField HOST_FILED = new ParseField( "host" );

    private static final ParseField ADDRESS_FILED = new ParseField( "transport_address" );

    private static final ParseField ROLES_FILED = new ParseField( "roles" );

    private static final ParseField SETTINGS_FILED = new ParseField( "settings" );

    private static final ObjectParser<Node, Void> PARSER = new ObjectParser<>( "get_nodes_response_node", true, Node::new );

    static
    {
        PARSER.declareString( Node::setName, NAME_FILED );
        PARSER.declareString( Node::setVersion, VERSION_FILED );
        PARSER.declareString( Node::setHostName, HOST_FILED );
        PARSER.declareString( Node::setAddress, ADDRESS_FILED );
        PARSER.declareStringArray( Node::setRoles, ROLES_FILED );
        PARSER.declareObject( Node::setSettings, ( parser, v ) -> NodeSettings.parse( parser ), SETTINGS_FILED );
    }

    public static Node parse( XContentParser parser, String name )
        throws IOException
    {
        Node node = PARSER.parse( parser, null );
        node.setId( name );

        return node;
    }

    private String id;

    private String name;

    private String version;

    private String hostName;

    private String address;

    private List<String> roles;

    private NodeSettings settings;

    public void setId( final String id )
    {
        this.id = id;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setVersion( final String version )
    {
        this.version = version;
    }

    public void setHostName( final String hostName )
    {
        this.hostName = hostName;
    }

    public void setAddress( final String address )
    {
        this.address = address;
    }

    public void setRoles( final List<String> roles )
    {
        this.roles = roles;
    }

    public void setSettings( final NodeSettings settings )
    {
        this.settings = settings;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getVersion()
    {
        return version;
    }

    public String getHostName()
    {
        return hostName;
    }

    public String getAddress()
    {
        return address;
    }

    public List<String> getRoles()
    {
        return roles;
    }

    public NodeSettings getSettings()
    {
        return settings;
    }

    public String getHostAddress()
    {
        if ( settings != null && settings.getNetwork() != null )
        {
            return settings.getNetwork().getHost();
        }

        return null;
    }

    public boolean isDataNode()
    {
        if ( roles != null )
        {
            return roles.contains( "data" ) || roles.contains( "d" );
        }

        return false;
    }

}
