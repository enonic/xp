package com.enonic.xp.admin.impl.market;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

class MarketRequestFactory
{

    public static Request create( final String baseUrl, final List<String> ids, final String version, final int start, final int count )
    {
        Map<String, Object> getParams = new HashMap<>();

        getParams.put( "xpVersion", version );
        getParams.put( "start", start );
        getParams.put( "count", count );

        ObjectMapper mapper = new ObjectMapper();
        String body = null;
        try
        {
            ObjectNode bodyNode = mapper.createObjectNode();
            if ( ids != null && ids.size() > 0 )
            {
                ArrayNode idsNode = bodyNode.putArray( "ids" );
                ids.forEach( idsNode::add );
            }
            body = mapper.writeValueAsString( bodyNode );
        }
        catch ( JsonProcessingException e )
        {
            e.printStackTrace();
        }

        return create( baseUrl, getParams, body );
    }

    private static Request create( final String baseUrl, Map<String, Object> getParams, String body )
    {
        final Request.Builder request = new Request.Builder();
        request.url( baseUrl );

        HttpUrl url = HttpUrl.parse( baseUrl );

        url = addParams( url, getParams );

        request.url( url );

        request.header( "Accept", "application/json" );

        request.post( RequestBody.create( MediaType.parse( "application/json" ), body ) );

        return request.build();
    }

    private static HttpUrl addParams( final HttpUrl url, final Map<String, Object> params )
    {
        HttpUrl.Builder urlBuilder = url.newBuilder();
        for ( Map.Entry<String, Object> header : params.entrySet() )
        {
            if ( header.getValue() != null )
            {
                urlBuilder.addEncodedQueryParameter( header.getKey(), header.getValue().toString() );
            }
        }
        return urlBuilder.build();
    }
}
