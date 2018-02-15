package com.enonic.xp.ignite.impl.reporter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.ignite.impl.IgniteAdminClient;
import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.status.StatusReporter;

@SuppressWarnings("unused")
@Component(immediate = true, service = StatusReporter.class)
public class IgniteClusterReporter
    extends JsonStatusReporter
{
    private IgniteAdminClient adminClient;

    @Override
    public JsonNode getReport()
    {
        return IgniteClusterReport.create().
            cluster( this.adminClient.getIgnite().cluster() ).
            build().
            toJson();
    }

    @Override
    public String getName()
    {
        return "cluster.ignite";
    }

    @Reference
    public void setAdminClient( final IgniteAdminClient adminClient )
    {
        this.adminClient = adminClient;
    }
}
