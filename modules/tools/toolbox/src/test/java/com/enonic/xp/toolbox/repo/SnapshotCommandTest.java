package com.enonic.xp.toolbox.repo;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import com.enonic.xp.toolbox.util.JsonHelper;

import static org.junit.Assert.*;

public class SnapshotCommandTest
    extends RepoCommandTest
{
    @Test
    public void testSnapshot()
        throws Exception
    {
        final SnapshotCommand command = new SnapshotCommand();
        configure( command );
        command.repository = "draft";

        addResponse( createResponseJson() );

        command.run();

        final RecordedRequest request = takeRequest();
        assertEquals( "POST", request.getMethod() );
        assertEquals( "/admin/rest/repo/snapshot", request.getPath() );
        assertEquals( JsonHelper.serialize( createRequestJson() ), request.getBody().readString( Charsets.UTF_8 ) );
    }

    private ObjectNode createRequestJson()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "repositoryId", "draft" );
        return json;
    }

    private ObjectNode createResponseJson()
    {
        // Should probably be populated with mock data.
        return JsonHelper.newObjectNode();
    }
}
