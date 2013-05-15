package com.enonic.wem.core.index.elastic;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.wem.core.index.IndexConstants;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class IndexMappingProviderTest
{
    @Test
    public void testLoadMappingFiles()
        throws Exception
    {
        final IndexMappingProvider mappingProvider = new IndexMappingProvider();
        final List<IndexMapping> indexMappings = mappingProvider.getMappingsForIndex( IndexConstants.WEM_INDEX );

        assertEquals( 3, indexMappings.size() );

        for ( final IndexMapping indexMapping : indexMappings )
        {
            assertEquals( IndexConstants.WEM_INDEX, indexMapping.getIndexName() );
            assertTrue( Lists.newArrayList( "account", "content", "binaries" ).contains( indexMapping.getIndexType() ) );
        }
    }
}
