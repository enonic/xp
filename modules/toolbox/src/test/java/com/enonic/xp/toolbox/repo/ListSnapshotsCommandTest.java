package com.enonic.xp.toolbox.repo;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import com.enonic.xp.toolbox.util.JsonHelper;

import static org.junit.Assert.*;

public class ListSnapshotsCommandTest
    extends RepoCommandTest
{
    @Test
    public void testList()
        throws Exception
    {
        final ListSnapshotsCommand command = new ListSnapshotsCommand();
        configure( command );

        addResponse( createResponseJson() );

        command.run();

        final RecordedRequest request = takeRequest();
        assertEquals( "GET", request.getMethod() );
        assertEquals( "/admin/rest/repo/list", request.getPath() );
    }

    private ObjectNode createResponseJson()
    {
        // Should probably be populated with mock data.
        return JsonHelper.newObjectNode();
    }
}
