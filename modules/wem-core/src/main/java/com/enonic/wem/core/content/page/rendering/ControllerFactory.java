package com.enonic.wem.core.content.page.rendering;


import java.io.IOException;
import java.nio.charset.Charset;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.core.rendering.Context;

final class ControllerFactory
{
    private final Client client;

    public ControllerFactory( final Client client )
    {
        this.client = client;
    }

    Controller create( final ModuleResourceKey javascriptResourceKey, final RootDataSet config, final Context context )
    {
        final Resource javascriptResource = retrieveJavascriptResource( javascriptResourceKey );
        final String javascriptSource = getJavascriptSource( javascriptResource );
        return new JavascriptController( javascriptSource, config, context );
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
