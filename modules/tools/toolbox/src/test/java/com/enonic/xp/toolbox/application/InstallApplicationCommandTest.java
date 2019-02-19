package com.enonic.xp.toolbox.application;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import com.enonic.xp.toolbox.repo.RepoCommandTest;
import com.enonic.xp.toolbox.util.JsonHelper;

import static org.junit.Assert.*;

public class InstallApplicationCommandTest
    extends RepoCommandTest
{
    @Rule
    public TemporaryFolder root = new TemporaryFolder();

    @Test
    public void install_file()
        throws Exception
    {
        final File file = root.newFile();

        final InstallApplicationCommand command = new InstallApplicationCommand();
        configure( command );
        command.file = file;

        addResponse( createResponseJson() );

        command.run();

        final RecordedRequest request = takeRequest();
        assertEquals( "POST", request.getMethod() );
        assertEquals( "/webapp/install", request.getPath() );
    }

    @Test
    public void install_url()
        throws Exception
    {
        final InstallApplicationCommand command = new InstallApplicationCommand();
        configure( command );
        command.url = "something://myUrl";

        addResponse( createResponseJson() );

        command.run();

        final RecordedRequest request = takeRequest();
        assertEquals( "POST", request.getMethod() );
        assertEquals( "/webapp/installUrl", request.getPath() );
        assertEquals( JsonHelper.serialize( createRequestJson() ), request.getBody().readString( Charsets.UTF_8 ) );
    }


    private ObjectNode createRequestJson()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "URL", "something://myUrl" );
        return json;
    }

    private ObjectNode createResponseJson()
    {
        // Should probably be populated with mock data.
        return JsonHelper.newObjectNode();
    }

}