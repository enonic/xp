package com.enonic.xp.core.impl.status;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.StatusReporter;

@Component(immediate = true)
public final class JvmGCReporter
    implements StatusReporter
{
    @Override
    public String getName()
    {
        return "jvm.gc";
    }

    @Override
    public ObjectNode getReport()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        long collectionTime = 0;
        long collectionCount = 0;
        final List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans();
        for ( final GarbageCollectorMXBean bean : beans )
        {
            collectionTime += bean.getCollectionTime();
            collectionCount += bean.getCollectionCount();
        }
        json.put( "collectionTime", collectionTime );
        json.put( "collectionCount", collectionCount );

        return json;
    }
}
