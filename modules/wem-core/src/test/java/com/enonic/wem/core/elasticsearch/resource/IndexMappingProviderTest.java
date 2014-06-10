package com.enonic.wem.core.elasticsearch.resource;

import java.util.List;

import org.junit.Test;

import com.enonic.wem.core.elasticsearch.resource.IndexMapping;
import com.enonic.wem.core.elasticsearch.resource.IndexMappingProvider;
import com.enonic.wem.core.index.Index;

import static junit.framework.Assert.assertEquals;

public class IndexMappingProviderTest
{
    @Test
    public void testLoadMappingFiles()
        throws Exception
    {
        final IndexMappingProvider mappingProvider = new IndexMappingProvider();
        final List<IndexMapping> indexMappings = mappingProvider.getMappingsForIndex( Index.SEARCH );

        assertEquals( 1, indexMappings.size() );
    }


    @Test
    public void tests()
        throws Exception
    {
        final IndexMappingProvider mappingProvider = new IndexMappingProvider();
        final List<IndexMapping> indexMappings = mappingProvider.getMappingsForIndex( Index.SEARCH );

        assertEquals( 1, indexMappings.size() );
    }
}
