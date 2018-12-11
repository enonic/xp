package com.enonic.xp.server.impl.status;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;

import org.osgi.service.component.annotations.Component;

import com.codahale.metrics.jvm.ThreadDump;
import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public final class ThreadDumpReporter
    implements StatusReporter
{
    private final ThreadDump threadDump;

    public ThreadDumpReporter()
    {
        this.threadDump = new ThreadDump( ManagementFactory.getThreadMXBean() );
    }

    @Override
    public String getName()
    {
        return "dump.threads";
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
        this.threadDump.dump( outputStream );
    }
}
