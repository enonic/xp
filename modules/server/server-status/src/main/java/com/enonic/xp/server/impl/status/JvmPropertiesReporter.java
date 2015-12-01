package com.enonic.xp.server.impl.status;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true)
public final class JvmPropertiesReporter
    implements StatusReporter
{
    @Override
    public String getName()
    {
        return "jvm.properties";
    }

    @Override
    public ObjectNode getReport()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        final RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        final Map<String, String> map = bean.getSystemProperties();

        final List<String> keys = Lists.newArrayList( map.keySet() );
        Collections.sort( keys );

        for ( final String key : keys )
        {
            json.put( key, map.get( key ) );
        }

        return json;
    }
}
