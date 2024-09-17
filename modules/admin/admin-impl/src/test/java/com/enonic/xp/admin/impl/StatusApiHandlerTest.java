package com.enonic.xp.admin.impl;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StatusApiHandlerTest
{
    @Test
    void testGetStatus()
        throws Exception
    {
        final StatusApiHandler handler = new StatusApiHandler();
        final WebResponse res = handler.handle( null );

        Map<String, Object> bodyAsMap = ObjectMapperHelper.create().readValue( res.getBody().toString(), Map.class );

        assertNotNull( bodyAsMap );
        assertEquals( VersionInfo.get().getVersion(), bodyAsMap.get( "version" ) );
        assertFalse( Boolean.parseBoolean( bodyAsMap.get( "readonly" ).toString() ) );
        assertNotNull( bodyAsMap.get( "context" ) );
        assertNotNull( bodyAsMap.get( "build" ) );
    }
}
