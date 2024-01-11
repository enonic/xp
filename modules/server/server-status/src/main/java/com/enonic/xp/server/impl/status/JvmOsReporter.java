package com.enonic.xp.server.impl.status;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public final class JvmOsReporter
    implements StatusReporter
{
    @Override
    public String getName()
    {
        return "jvm.os";
    }

    @Override
    public MediaType getMediaType()
    {
        return MediaType.JSON_UTF_8;
    }

    @Override
    public void report( final OutputStream outputStream )
        throws IOException
    {
        outputStream.write( getReport().toString().getBytes( StandardCharsets.UTF_8 ) );
    }

    public JsonNode getReport()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        final OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
        json.put( "name", bean.getName() );
        json.put( "version", bean.getVersion() );
        json.put( "arch", bean.getArch() );
        json.put( "cores", bean.getAvailableProcessors() );
        json.put( "loadAverage", bean.getSystemLoadAverage() );

        return json;
    }
}
