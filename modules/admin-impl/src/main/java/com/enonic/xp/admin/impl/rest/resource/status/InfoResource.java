package com.enonic.xp.admin.impl.rest.resource.status;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.web.jaxrs.JaxRsResource;
import com.enonic.xp.server.ServerInfo;

@Path("status")
@Component(immediate = true)
public final class InfoResource
    implements JaxRsResource
{
    private ServerInfo serverInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getStatus()
    {
        return createStatus().toString();
    }

    private ObjectNode createStatus()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        json.set( "xp", buildProductInfo() );
        json.set( "os", buildOSInfo() );
        json.set( "jvm", buildJVMInfo() );
        json.set( "memory", buildMemoryInfo() );
        json.set( "gc", buildGCInfo() );

        return json;
    }

    @Reference
    public void setServerInfo( final ServerInfo serverInfo )
    {
        this.serverInfo = serverInfo;
    }

    private ObjectNode buildProductInfo()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        new ProductInfoBuilder( this.serverInfo ).build( json );
        return json;
    }

    private ObjectNode buildOSInfo()
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

    private ObjectNode buildJVMInfo()
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

    private ObjectNode buildGCInfo()
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

    private ObjectNode buildMemoryInfo()
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        final MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
        json.set( "heap", buildMemoryUsageInfo( bean.getHeapMemoryUsage() ) );
        json.set( "nonHeap", buildMemoryUsageInfo( bean.getNonHeapMemoryUsage() ) );

        return json;
    }

    private ObjectNode buildMemoryUsageInfo( final MemoryUsage mem )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "init", mem.getInit() );
        json.put( "max", mem.getMax() );
        json.put( "committed", mem.getCommitted() );
        json.put( "used", mem.getUsed() );
        return json;
    }
}
