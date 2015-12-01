package com.enonic.xp.elasticsearch.impl.status.index;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.StatusReporter;


@Component(immediate = true)
public class IndexReporter
    implements StatusReporter
{
    private IndexReportProvider indexReportProvider;

    @Override
    public String getName()
    {
        return "index";
    }

    @Override
    public ObjectNode getReport()
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
