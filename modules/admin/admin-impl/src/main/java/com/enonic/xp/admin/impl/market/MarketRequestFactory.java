package com.enonic.xp.admin.impl.market;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

class MarketRequestFactory
{
    private static final Duration READ_TIMEOUT = Duration.ofSeconds( 10 );

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static HttpRequest create( final String baseUrl, final List<String> ids, final String version, final int start, final int count )
    {
        Map<String, String> getParams = Map.of( "xpVersion", version, "start", String.valueOf( start ), "count", String.valueOf( count ) );

        String body = null;
        try
        {
            ObjectNode bodyNode = MAPPER.createObjectNode();
            if ( ids != null && ids.size() > 0 )
            {
                ArrayNode idsNode = bodyNode.putArray( "ids" );
                ids.forEach( idsNode::add );
            }
            body = MAPPER.writeValueAsString( bodyNode );
        }
        catch ( JsonProcessingException e )
        {
            e.printStackTrace();
        }

        return create( baseUrl, getParams, body );
    }

    private static HttpRequest create( final String baseUrl, Map<String, String> getParams, String body )
    {
        final String queryString = getParams.entrySet().stream().
            map( e -> URLEncoder.encode( e.getKey(), StandardCharsets.UTF_8 ) + "=" +
                URLEncoder.encode( e.getValue(), StandardCharsets.UTF_8 ) ).
            collect( Collectors.joining( "&" ) );
        final URI uri = URI.create( baseUrl + "?" + queryString );

        return HttpRequest.newBuilder( uri ).
            timeout( READ_TIMEOUT ).
            header( "Content-Type", "application/json" ).
            header( "Accept", "application/json" ).
            header( "Accept-Encoding", "gzip" ).
            POST( HttpRequest.BodyPublishers.ofString( body ) ).
            build();
    }
}
