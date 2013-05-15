package com.enonic.wem.core.index.elastic;

import java.util.List;

import org.junit.Test;

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
