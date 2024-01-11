package com.enonic.xp.server.impl.status;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public final class JvmInfoReporter
    implements StatusReporter
{
    @Override
    public String getName()
    {
        return "jvm.info";
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

        final RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        json.put( "name", bean.getVmName() );
        json.put( "vendor", bean.getVmVendor() );
        json.put( "version", bean.getVmVersion() );
        json.put( "startTime", bean.getStartTime() );
        json.put( "upTime", bean.getUptime() );

        return json;
    }
}
