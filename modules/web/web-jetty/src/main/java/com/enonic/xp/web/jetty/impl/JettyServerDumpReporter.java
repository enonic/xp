package com.enonic.xp.web.jetty.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.jetty.server.Server;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true)
public final class JettyServerDumpReporter
    implements StatusReporter
{
    private final Server server;

    @Activate
    public JettyServerDumpReporter( @Reference final Server server )
    {
        this.server = server;
    }

    @Override
    public String getName()
    {
        return "http.serverdump";
    }

    @Override
    public MediaType getMediaType()
    {
        return MediaType.PLAIN_TEXT_UTF_8;
    }

    @Override
    public void report( final OutputStream outputStream )
        throws IOException
    {
        outputStream.write( server.dump().getBytes( StandardCharsets.UTF_8 ) );
    }
}
