package com.enonic.xp.elasticsearch.impl.status.index;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;


@Component(immediate = true, service = StatusReporter.class)
public class IndexReporter
    implements StatusReporter
{
    private final IndexReportProvider indexReportProvider;

    @Activate
    public IndexReporter( @Reference final IndexReportProvider indexReportProvider )
    {
        this.indexReportProvider = indexReportProvider;
    }

    @Override
    public MediaType getMediaType()
    {
        return MediaType.JSON_UTF_8;
    }

    @Override
    public void report( final OutputStream outputStream )
        throws IOException
    {
        outputStream.write( getReport().toString().getBytes( StandardCharsets.UTF_8 ) );
    }

    @Override
    public String getName()
    {
        return "index";
    }

    private JsonNode getReport()
    {
        return indexReportProvider.getInfo().
            toJson();
    }
}
