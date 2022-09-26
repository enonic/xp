package com.enonic.xp.portal.impl.mapper;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.script.serializer.JsonMapGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PortalMapperServiceTest
{
    @Test
    void lowercaseRequestHeaders()
    {
        final PortalConfig portalConfig = mock( PortalConfig.class );

        when( portalConfig.lowercaseRequestHeaders() ).thenReturn( false );
        new PortalMapperService().activate( portalConfig );

        final JsonMapGenerator generator = new JsonMapGenerator();
        final PortalRequest request = new PortalRequest();
        request.getHeaders().put( "X-Header", "value" );

        new PortalRequestMapper( request ).serialize( generator );
        JsonNode actualJson = (JsonNode) generator.getRoot();

        assertEquals( "value", actualJson.get( "headers" ).get( "X-Header" ).asText() );
        assertFalse( actualJson.get( "headers" ).has( "x-header" ) );

        when( portalConfig.lowercaseRequestHeaders() ).thenReturn( true );
        new PortalMapperService().activate( portalConfig );

        new PortalRequestMapper( request ).serialize( generator );
        actualJson = (JsonNode) generator.getRoot();

        assertEquals( "value", actualJson.get( "headers" ).get( "x-header" ).asText() );
        assertFalse( actualJson.get( "headers" ).has( "X-Header" ) );
    }

    @Test
    void lowercaseResponseHeaders()
    {
        final PortalConfig portalConfig = mock( PortalConfig.class );

        when( portalConfig.lowercaseResponseHeaders() ).thenReturn( false );
        new PortalMapperService().activate( portalConfig );

        final JsonMapGenerator generator = new JsonMapGenerator();
        final PortalResponse response = PortalResponse.create().header( "X-Header", "value" ).build();

        new PortalResponseMapper( response ).serialize( generator );
        JsonNode actualJson = (JsonNode) generator.getRoot();

        assertEquals( "value", actualJson.get( "headers" ).get( "X-Header" ).asText() );
        assertFalse( actualJson.get( "headers" ).has( "x-header" ) );

        when( portalConfig.lowercaseResponseHeaders() ).thenReturn( true );
        new PortalMapperService().activate( portalConfig );

        new PortalResponseMapper( response ).serialize( generator );
        actualJson = (JsonNode) generator.getRoot();

        assertEquals( "value", actualJson.get( "headers" ).get( "x-header" ).asText() );
        assertFalse( actualJson.get( "headers" ).has( "X-Header" ) );
    }
}
