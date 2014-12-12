package com.enonic.wem.admin.status;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;

public final class PropertiesStatusInfoBuilder
    extends StatusInfoBuilder
{
    public PropertiesStatusInfoBuilder()
    {
        super( "systemProperties" );
    }

    @Override
    public void build( final ObjectNode json )
    {
        final RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        for ( final Map.Entry<String, String> entry : bean.getSystemProperties().entrySet() )
        {
            json.put( entry.getKey(), entry.getValue() );
        }
    }
}
