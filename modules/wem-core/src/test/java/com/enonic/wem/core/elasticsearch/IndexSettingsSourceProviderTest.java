package com.enonic.wem.core.elasticsearch;

import java.util.List;

import org.junit.Test;

import com.enonic.wem.core.elasticsearch.resource.IndexSettingsSourceProvider;

import static junit.framework.Assert.assertEquals;


public class IndexSettingsSourceProviderTest
{
    @Test
    public void testGetSources()
        throws Exception
    {
        final List<String> sources = new IndexSettingsSourceProvider().getSources();
        assertEquals( 1, sources.size() );
    }
}
