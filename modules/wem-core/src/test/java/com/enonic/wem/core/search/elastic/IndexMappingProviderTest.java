package com.enonic.wem.core.search.elastic;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.google.common.collect.Lists;

import com.enonic.wem.core.search.IndexConstants;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class IndexMappingProviderTest
    extends IndexConstants
{
    @Test
    public void testLoadMappingFiles()
        throws Exception
    {
        final ResourcePatternResolver resourcePatternResolver = Mockito.mock( ResourcePatternResolver.class );

        Resource[] resources = new Resource[]{new ClassPathResource( "com/enonic/wem/core/search/elastic/wem-content-mapping.json" ),
            new ClassPathResource( "com/enonic/wem/core/search/elastic/wem-account-mapping.json" ),
            new ClassPathResource( "com/enonic/wem/core/search/elastic/wem-relations-mapping.json" )};

        Mockito.when( resourcePatternResolver.getResources( IndexMappingProvider.MAPPING_RESOURCE_LOCATION ) ).thenReturn( resources );
        IndexMappingProvider mappingProvider = new IndexMappingProvider();
        mappingProvider.setResourcePatternResolver( resourcePatternResolver );
        mappingProvider.init();

        final List<IndexMapping> indexMappings = mappingProvider.getMappingsForIndex( WEM_INDEX );

        assertEquals( 3, indexMappings.size() );

        for ( IndexMapping indexMapping : indexMappings )
        {
            assertEquals( WEM_INDEX, indexMapping.getIndexName() );
            assertTrue( Lists.newArrayList( "account", "content", "relations" ).contains( indexMapping.getIndexType() ) );
        }
    }

    @Test
    public void testContentMapping()
        throws Exception
    {
        String expected = "{\n" +
            "    \"content\":{\n" +
            "        \"_all\":{\n" +
            "            \"enabled\":false\n" +
            "        }\n" +
            "    }\n" +
            "}";

        final ResourcePatternResolver resourcePatternResolver = Mockito.mock( ResourcePatternResolver.class );

        Resource[] resources = new Resource[]{new ClassPathResource( "com/enonic/wem/core/search/elastic/wem-content-mapping.json" )};

        Mockito.when( resourcePatternResolver.getResources( IndexMappingProvider.MAPPING_RESOURCE_LOCATION ) ).thenReturn( resources );
        IndexMappingProvider mappingProvider = new IndexMappingProvider();
        mappingProvider.setResourcePatternResolver( resourcePatternResolver );
        mappingProvider.init();

        final List<IndexMapping> indexMappings = mappingProvider.getMappingsForIndex( WEM_INDEX );

        assertEquals( 1, indexMappings.size() );

        final IndexMapping contentMapping = indexMappings.iterator().next();
        assertEquals( expected, contentMapping.getSource() );
    }

    @Test
    public void testInvalidMappingResourceName()
        throws Exception
    {
        final ResourcePatternResolver resourcePatternResolver = Mockito.mock( ResourcePatternResolver.class );

        Resource[] resources = new Resource[]{new ClassPathResource( "com/enonic/wem/core/search/elastic/content-mapping.json" )};

        Mockito.when( resourcePatternResolver.getResources( IndexMappingProvider.MAPPING_RESOURCE_LOCATION ) ).thenReturn( resources );
        IndexMappingProvider mappingProvider = new IndexMappingProvider();
        mappingProvider.setResourcePatternResolver( resourcePatternResolver );
        mappingProvider.init();

        final List<IndexMapping> indexMappings = mappingProvider.getMappingsForIndex( WEM_INDEX );

        assertEquals( 0, indexMappings.size() );
    }

    @Test
    public void doNotLoadOtherIndexMapping()
        throws Exception
    {
        final ResourcePatternResolver resourcePatternResolver = Mockito.mock( ResourcePatternResolver.class );

        Resource[] resources =
            new Resource[]{new ClassPathResource( "com/enonic/wem/core/search/elastic/otherindex-account-mapping.json" )};

        Mockito.when( resourcePatternResolver.getResources( IndexMappingProvider.MAPPING_RESOURCE_LOCATION ) ).thenReturn( resources );
        IndexMappingProvider mappingProvider = new IndexMappingProvider();
        mappingProvider.setResourcePatternResolver( resourcePatternResolver );
        mappingProvider.init();

        final List<IndexMapping> indexMappings = mappingProvider.getMappingsForIndex( WEM_INDEX );

        assertEquals( 0, indexMappings.size() );
    }

}
