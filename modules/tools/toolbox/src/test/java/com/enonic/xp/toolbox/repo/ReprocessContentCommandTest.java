package com.enonic.xp.toolbox.repo;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import com.enonic.xp.toolbox.util.JsonHelper;

import static org.junit.Assert.*;

public class ReprocessContentCommandTest
    extends RepoCommandTest
{
    @Test
    public void testReprocessContent()
        throws Exception
    {
        final ReprocessContentCommand command = new ReprocessContentCommand();
        configure( command );
        command.skipChildren = true;
        command.sourceBranchPath = "master:/root/node1";

        addResponse( createResponseJson() );

        command.run();

        final RecordedRequest request = takeRequest();
        assertEquals( "POST", request.getMethod() );
        assertEquals( "/content/reprocess", request.getPath() );
        assertEquals( JsonHelper.serialize( createRequestJson() ), request.getBody().readString( Charsets.UTF_8 ) );
    }

    private ObjectNode createRequestJson()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "sourceBranchPath", "master:/root/node1" );
        json.put( "skipChildren", true );
        return json;
    }

    private ObjectNode createResponseJson()
    {
        return JsonHelper.newObjectNode();
    }
}
