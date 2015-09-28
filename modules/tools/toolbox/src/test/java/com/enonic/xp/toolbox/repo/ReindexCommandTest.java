package com.enonic.xp.toolbox.repo;

import java.util.Arrays;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import com.enonic.xp.toolbox.util.JsonHelper;

import static org.junit.Assert.*;

public class ReindexCommandTest
    extends RepoCommandTest
{
    @Test
    public void testReindex()
        throws Exception
    {
        final ReindexCommand command = new ReindexCommand();
        configure( command );
        command.branches = Arrays.asList( "draft", "master" );
        command.repository = "draft";

        addResponse( createResponseJson() );

        command.run();

        final RecordedRequest request = takeRequest();
        assertEquals( "POST", request.getMethod() );
        assertEquals( "/admin/rest/repo/reindex", request.getPath() );
        assertEquals( JsonHelper.serialize( createRequestJson() ), request.getBody().readString( Charsets.UTF_8 ) );
    }

    private ObjectNode createRequestJson()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "repository", "draft" );
        json.put( "initialize", false );
        json.put( "branches", "draft,master" );
        return json;
    }

    private ObjectNode createResponseJson()
    {
        // Should probably be populated with mock data.
        return JsonHelper.newObjectNode();
    }
}
