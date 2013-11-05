package com.enonic.wem.core.rendering;


import java.io.IOException;
import java.nio.charset.Charset;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;

public final class ControllerFactory
{
    private final Client client;

    public ControllerFactory( final Client client )
    {
        this.client = client;
    }

    public Controller create( final ModuleResourceKey javascriptResourceKey, final RootDataSet config )
    {
        final Resource javascriptResource = retrieveJavascriptResource( javascriptResourceKey );
        final String javascriptSource = getJavascriptSource( javascriptResource );
        final Controller controller = new Controller( javascriptSource, config );
        return controller;
    }

    private Resource retrieveJavascriptResource( final ModuleResourceKey javascriptResourceKey )
    {
        final GetModuleResource getResourceCommand = Commands.module().getResource().resourceKey( javascriptResourceKey );
        return client.execute( getResourceCommand );
    }

    private String getJavascriptSource( final Resource javascriptResource )
    {
        try
        {
            return javascriptResource.getByteSource().asCharSource( Charset.forName( "UTF-8" ) ).read();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }
}
