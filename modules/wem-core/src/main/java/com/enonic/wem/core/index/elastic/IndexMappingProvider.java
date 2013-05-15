package com.enonic.wem.core.index.elastic;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import com.enonic.wem.core.index.IndexException;

@Component
public class IndexMappingProvider
{
    private final static String PREFIX = "META-INF/index/mapping/";

    private final static String[] MAPPING_FILES = { //
        PREFIX + "wem-account-mapping.json", //
        PREFIX + "wem-binaries-mapping.json", //
        PREFIX + "wem-content-mapping.json" //
    };

    public List<IndexMapping> getMappingsForIndex( final String indexName )
    {
        final List<IndexMapping> indexMappings = Lists.newArrayList();
        for ( final String mappingFile : MAPPING_FILES )
        {
            try
            {
                final IndexMapping indexMapping = createIndexMapping( indexName, mappingFile );
                if ( indexMapping != null )
                {
                    indexMappings.add( indexMapping );
                }
            }
            catch ( IOException e )
            {
                throw new IndexException( "Failed to load mapping file: " + mappingFile, e );
            }
        }
        return indexMappings;
    }

    private IndexMapping createIndexMapping( final String indexName, final String mappingFile )
        throws IOException
    {
        final String filename = mappingFile.substring( PREFIX.length() );

        final String[] parts = filename.split( "-" );
        if ( parts.length < 3 )
        {
            return null;
        }

        final String resourceIndexName = parts[0];
        if ( !indexName.equals( resourceIndexName ) )
        {
            return null;
        }

        final String indexType = parts[1];
        final URL url = Resources.getResource( mappingFile );
        final String mapping = Resources.toString( url, Charsets.UTF_8 );

        return new IndexMapping( resourceIndexName, indexType, mapping );
    }
}
