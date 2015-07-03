package com.enonic.xp.toolbox.repo;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import com.enonic.xp.toolbox.util.JsonHelper;

import static org.junit.Assert.*;

public class LoadCommandTest
    extends RepoCommandTest
{
    @Test
    public void testLoad()
        throws Exception
    {
        final LoadCommand command = new LoadCommand();
        configure( command );
        command.source = "source-path";

        addResponse( createResponseJson() );

        command.run();

        final RecordedRequest request = takeRequest();
        assertEquals( "POST", request.getMethod() );
        assertEquals( "/admin/rest/system/load", request.getPath() );
        assertEquals( JsonHelper.serialize( createRequestJson() ), request.getBody().readString( Charsets.UTF_8 ) );
    }

    private ObjectNode createRequestJson()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "name", "source-path" );
        return json;
    }

    private ObjectNode createResponseJson()
    {
        // Should probably be populated with mock data.
        return JsonHelper.newObjectNode();
    }
}
