package com.enonic.xp.admin.impl;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StatusApiHandlerTest
{
    @Test
    void testGetStatus()
        throws Exception
    {
        final StatusApiHandler handler = new StatusApiHandler();
        final WebRequest request = new WebRequest();
        request.setRawPath( "/_/admin:status" );
        final WebResponse res = handler.handle( request );

        Map<String, Object> bodyAsMap = ObjectMapperHelper.create().readValue( res.getBody().toString(), Map.class );

        assertNotNull( bodyAsMap );
        assertEquals( VersionInfo.get().getVersion(), bodyAsMap.get( "version" ) );
        assertNotNull( bodyAsMap.get( "context" ) );
        assertNotNull( bodyAsMap.get( "build" ) );
    }
}
