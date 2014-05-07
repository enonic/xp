package com.enonic.wem.core.elastic;

import java.util.List;

import org.junit.Test;

import com.enonic.wem.core.index.Index;

import static junit.framework.Assert.assertEquals;

public class IndexMappingProviderTest
{
    @Test
    public void testLoadMappingFiles()
        throws Exception
    {
        final IndexMappingProvider mappingProvider = new IndexMappingProvider();
        final List<IndexMapping> indexMappings = mappingProvider.getMappingsForIndex( Index.NODB );

        assertEquals( 1, indexMappings.size() );
    }
}
