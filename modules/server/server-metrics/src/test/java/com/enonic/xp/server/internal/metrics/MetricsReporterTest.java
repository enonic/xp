package com.enonic.xp.server.internal.metrics;

import org.junit.jupiter.api.Test;

import com.google.common.net.MediaType;

import com.enonic.xp.status.BaseReporterTest;

import static org.assertj.core.api.Assertions.assertThat;

public class MetricsReporterTest
    extends BaseReporterTest<MetricsReporter>
{
    public MetricsReporterTest()
    {
        super( "metrics", MediaType.parse( "application/openmetrics-text; version=1.0.0; charset=utf-8" ));
    }

    @Override
    protected MetricsReporter newReporter()
        throws Exception
    {
        return new MetricsReporter();
    }

    @Test
    public void testReport()
        throws Exception
    {
        assertThat(textReport()).endsWith( "# EOF\n" );
    }
}
