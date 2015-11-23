package com.enonic.xp.server.impl.status;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true)
public final class JvmOsReporter
    implements StatusReporter
{
    @Override
    public String getName()
    {
        return "jvm.os";
    }

    @Override
    public ObjectNode getReport()
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
