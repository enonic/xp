package com.enonic.xp.toolbox.repo;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import com.enonic.xp.toolbox.util.JsonHelper;

import static org.junit.Assert.*;

public class ImportCommandTest
    extends RepoCommandTest
{
    @Test
    public void testImport()
        throws Exception
    {
        final ImportCommand command = new ImportCommand();
        configure( command );
        command.exportName = "myexport";
        command.targetRepoPath = "<draft>:<master>:<root/node1>";

        addResponse( createResponseJson() );

        command.run();

        final RecordedRequest request = takeRequest();
        assertEquals( "POST", request.getMethod() );
        assertEquals( "/admin/rest/export/import", request.getPath() );
        assertEquals( JsonHelper.serialize( createRequestJson() ), request.getBody().readString( Charsets.UTF_8 ) );
    }

    private ObjectNode createRequestJson()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "exportName", "myexport" );
        json.put( "targetRepoPath", "<draft>:<master>:<root/node1>" );
        json.put( "importWithIds", true );
        json.put( "importWithPermissions", true );
        return json;
    }

    private ObjectNode createResponseJson()
    {
        // Should probably be populated with mock data.
        return JsonHelper.newObjectNode();
    }
}
