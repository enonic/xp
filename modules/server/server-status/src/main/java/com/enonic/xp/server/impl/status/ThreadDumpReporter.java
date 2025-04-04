package com.enonic.xp.server.impl.status;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Component;

import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public final class ThreadDumpReporter
    implements StatusReporter
{

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
        for ( ThreadInfo threadInfo : ManagementFactory.getThreadMXBean().dumpAllThreads( true, true ) )
        {
            outputStream.write( threadInfo.toString().getBytes( StandardCharsets.UTF_8 ) );
        }
    }
}
