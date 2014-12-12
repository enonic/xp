package com.enonic.wem.admin.status;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import com.fasterxml.jackson.databind.node.ObjectNode;

public final class JVMStatusInfoBuilder
    extends StatusInfoBuilder
{
    public JVMStatusInfoBuilder()
    {
        super( "jvm" );
    }

    @Override
    public void build( final ObjectNode json )
    {
        final RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        json.put( "name", bean.getVmName() );
        json.put( "vendor", bean.getVmVendor() );
        json.put( "version", bean.getVmVersion() );
        json.put( "startTime", bean.getStartTime() );
        json.put( "upTime", bean.getUptime() );
    }
}
