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

    private static final ObjectParser<Builder, Void> PARSER = new ObjectParser<>( "get_nodes_response_node", true, Builder::new );

    static
    {
        PARSER.declareString( Builder::name, NAME_FILED );
        PARSER.declareString( Builder::version, VERSION_FILED );
        PARSER.declareString( Builder::hostName, HOST_FILED );
        PARSER.declareString( Builder::address, ADDRESS_FILED );
        PARSER.declareStringArray( Builder::roles, ROLES_FILED );
    }

    public static Node parse( XContentParser parser, String name )
        throws IOException
    {
        final Builder builder = PARSER.parse( parser, null );
        builder.id( name );

        return builder.build();
    }

    private final String id;

    private final String name;

    private final String version;

    private final String hostName;

    private final String address;

    private final List<String> roles;

    private Node( final Builder builder )
    {
        this.id = builder.id;
        this.name = builder.name;
        this.version = builder.version;
        this.hostName = builder.hostName;
        this.address = builder.address;
        this.roles = builder.roles;
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

    public boolean isMasterNode()
    {
        if ( roles != null )
        {
            return roles.contains( "master" ) || roles.contains( "m" );
        }

        return false;
    }

    public boolean isDataNode()
    {
        if ( roles != null )
        {
            return roles.contains( "data" ) || roles.contains( "d" );
        }

        return false;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    @Override
    public String toString()
    {
        return "Node{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", version='" + version + '\'' + ", hostName='" + hostName + '\'' +
            ", address='" + address + '\'' + ", roles=" + roles + '}';
    }

    public static class Builder
    {

        private String id;

        private String name;

        private String version;

        private String hostName;

        private String address;

        private List<String> roles;

        public Builder()
        {

        }

        public Builder id( final String id )
        {
            this.id = id;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder version( final String version )
        {
            this.version = version;
            return this;
        }

        public Builder hostName( final String hostName )
        {
            this.hostName = hostName;
            return this;
        }

        public Builder address( final String address )
        {
            this.address = address;
            return this;
        }

        public Builder roles( final List<String> roles )
        {
            this.roles = roles;
            return this;
        }

        public Node build()
        {
            return new Node( this );
        }

    }

}
