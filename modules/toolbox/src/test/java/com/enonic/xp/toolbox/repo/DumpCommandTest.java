package com.enonic.xp.toolbox.repo;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import com.enonic.xp.toolbox.util.JsonHelper;

import static org.junit.Assert.*;

public class DumpCommandTest
    extends RepoCommandTest
{
    @Test
    public void testDump()
        throws Exception
    {
        final DumpCommand command = new DumpCommand();
        configure( command );
        command.target = "target-path";

        addResponse( createResponseJson() );

        command.run();

        final RecordedRequest request = takeRequest();
        assertEquals( "POST", request.getMethod() );
        assertEquals( "/admin/rest/system/dump", request.getPath() );
        assertEquals( JsonHelper.serialize( createRequestJson() ), request.getBody().readString( Charsets.UTF_8 ) );
    }

    private ObjectNode createRequestJson()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "targetDirectory", "target-path" );
        return json;
    }

    private ObjectNode createResponseJson()
    {
        // Should probably be populated with mock data.
        return JsonHelper.newObjectNode();
    }
}
