package com.enonic.xp.elasticsearch.impl.status.index;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.status.StatusReporter;


@Component(immediate = true, service = StatusReporter.class)
public class IndexReporter
    extends JsonStatusReporter
{
    private IndexReportProvider indexReportProvider;

    @Override
    public String getName()
    {
        return "index";
    }

    @Override
    public JsonNode getReport()
    {
        return indexReportProvider.getInfo().
            toJson();
    }

    @Reference
    public void setIndexReportProvider( final IndexReportProvider indexReportProvider )
    {
        this.indexReportProvider = indexReportProvider;
    }
}
