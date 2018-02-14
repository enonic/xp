package com.enonic.xp.ignite.impl.reporter;

import org.apache.ignite.Ignite;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public class IgniteClusterReporter
    extends JsonStatusReporter
{
    private Ignite ignite;

    @Override
    public JsonNode getReport()
    {
        return IgniteClusterReport.create().
            cluster( this.ignite.cluster() ).
            regionMetrics( this.ignite.dataRegionMetrics() ).
            storageMetrics( this.ignite.dataStorageMetrics() ).
            build().
            toJson();

    }

    @Override
    public String getName()
    {
        return "cluster.ignite";
    }

    @Reference
    public void setIgnite( final Ignite ignite )
    {
        this.ignite = ignite;
    }
}
