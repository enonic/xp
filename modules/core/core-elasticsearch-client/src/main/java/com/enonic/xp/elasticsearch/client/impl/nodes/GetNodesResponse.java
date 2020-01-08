package com.enonic.xp.elasticsearch.client.impl.nodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ConstructingObjectParser;
import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;

public class GetNodesResponse
{
    private static final ParseField CLUSTER_NAME_FIELD = new ParseField( "cluster_name" );

    private static final ParseField NODES_FIELD = new ParseField( "nodes" );

    @SuppressWarnings("unchecked")
    private static final ConstructingObjectParser<GetNodesResponse, String> PARSER =
        new ConstructingObjectParser<>( "get_nodes_response", true,
                                        args -> new GetNodesResponse( (String) args[0], (List<Node>) args[1] ) );

    static
    {
        PARSER.declareString( ConstructingObjectParser.constructorArg(), CLUSTER_NAME_FIELD );
        PARSER.declareObject( ConstructingObjectParser.constructorArg(), ( parser, v ) -> {
            final List<Node> nodes = new ArrayList<>();

            XContentParser.Token token;
            while ( !( ( token = parser.nextToken() ) == XContentParser.Token.END_OBJECT &&
                NODES_FIELD.getPreferredName().equals( parser.currentName() ) ) )
            {
                nodes.add( Node.parse( parser, parser.currentName() ) );
            }

            return nodes;
        }, NODES_FIELD );
    }

    private final String clusterName;

    private final List<Node> nodes;

    public GetNodesResponse( final String clusterName, final List<Node> nodes )
    {
        this.clusterName = clusterName;
        this.nodes = nodes;
    }

    public String getClusterName()
    {
        return clusterName;
    }

    public List<Node> getNodes()
    {
        return nodes;
    }

    @Override
    public String toString()
    {
        return "GetNodesResponse{" + "clusterName='" + clusterName + '\'' + ", nodes=" + nodes + '}';
    }

    public static GetNodesResponse fromResponse( final Response response )
        throws IOException
    {
        final HttpEntity entity = response.getEntity();
        if ( entity == null )
        {
            throw new IllegalStateException( "Response body expected but not returned" );
        }

        if ( entity.getContentType() == null )
        {
            throw new IllegalStateException( "Elasticsearch didn't return the [Content-Type] header, unable to parse response body" );
        }

        final XContentType xContentType = XContentType.fromMediaTypeOrFormat( entity.getContentType().getValue() );
        if ( xContentType == null )
        {
            throw new IllegalStateException( "Unsupported Content-Type: " + entity.getContentType().getValue() );
        }

        try (final InputStream stream = response.getEntity().getContent();

             final XContentParser parser = XContentFactory.xContent( xContentType ).
                 createParser( NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, stream ))
        {
            return fromXContent( parser );
        }
    }

    public static GetNodesResponse fromXContent( final XContentParser parser )
        throws IOException
    {
        return PARSER.parse( parser, null );
    }

}
