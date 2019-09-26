package com.enonic.xp.elasticsearch7.impl;

import java.io.IOException;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(immediate = true)
public class Elasticsearch7Activator
{
    private Process process;

    @Activate
    public void activate( final BundleContext context, final Map<String, String> map )
        throws IOException
    {
        process =
            new ProcessBuilder( "C:\\es7\\elasticsearch-oss-7.3.2-windows-x86_64\\elasticsearch-7.3.2\\bin\\elasticsearch.bat" ).
                inheritIO().
                start();
    }

    @Deactivate
    public void deactivate()
    {
        process.destroy();
    }
}
