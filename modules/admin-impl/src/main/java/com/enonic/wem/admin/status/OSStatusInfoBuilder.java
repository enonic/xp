package com.enonic.wem.admin.status;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import com.fasterxml.jackson.databind.node.ObjectNode;

public final class OSStatusInfoBuilder
    extends StatusInfoBuilder
{
    public OSStatusInfoBuilder()
    {
        super( "os" );
    }

    @Override
    public void build( final ObjectNode json )
    {
        final OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
        json.put( "name", bean.getName() );
        json.put( "version", bean.getVersion() );
        json.put( "arch", bean.getArch() );
        json.put( "cores", bean.getAvailableProcessors() );
        json.put( "loadAverage", bean.getSystemLoadAverage() );
    }
}
