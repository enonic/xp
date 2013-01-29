package com.enonic.wem.core.search.elastic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

import com.enonic.wem.core.search.IndexException;

@Component
public class IndexMappingProvider
{
    public static final String MAPPING_RESOURCE_LOCATION =
        ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "/META-INF/index/mapping/*-mapping.json";

    private List<Resource> resources;

    private ResourcePatternResolver resourcePatternResolver;

    @PostConstruct
    public void init()
        throws Exception
    {
        this.resources = Lists.newArrayList( resourcePatternResolver.getResources( MAPPING_RESOURCE_LOCATION ) );
    }

    public List<IndexMapping> getMappingsForIndex( final String indexName )
    {
        List<IndexMapping> indexMappings = Lists.newArrayList();

        for ( Resource resource : resources )
        {
            try
            {
                final IndexMapping indexMapping = createIndexMapping( resource );
                if ( indexMapping != null )
                {
                    indexMappings.add( indexMapping );
                }
            }
            catch ( IOException e )
            {
                throw new IndexException( "Failed to load mapping files from: " + MAPPING_RESOURCE_LOCATION, e );
            }
        }
        return indexMappings;
    }

    private IndexMapping createIndexMapping( final Resource resource )
        throws IOException
    {
        if ( !resource.exists() )
        {
            return null;
        }

        final String filename = resource.getFilename();

        final String[] parts = filename.split( "-" );

        if ( parts.length < 3 )
        {
            return null;
        }

        final String indexName = parts[0];
        final String indexType = parts[1];
        final String mapping = CharStreams.toString( new InputStreamReader( resource.getInputStream(), Charsets.UTF_8 ) );

        return new IndexMapping( indexName, indexType, mapping );
    }

    @Autowired
    public void setResourcePatternResolver( final ResourcePatternResolver resourcePatternResolver )
    {
        this.resourcePatternResolver = resourcePatternResolver;
    }
}
