package com.enonic.wem.core.elasticsearch.resource;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexException;


public class IndexMappingProvider
{
    private final static String PREFIX = "/META-INF/index/mapping/";

    private final static String[] MAPPING_FILES = { //
        PREFIX + "workspace-node-mapping.json", //
        PREFIX + "store-entity-mapping.json", //
        PREFIX + "version-node-mapping.json"//
    };

    private final static String[] TEMPLATE_FILES = { //
        PREFIX + "search-node-template.json"};

    List<IndexMapping> getMappingsForIndex( final Index index )
    {
        final List<IndexMapping> indexMappings = Lists.newArrayList();
        for ( final String mappingFile : MAPPING_FILES )
        {
            doGetMapping( index, indexMappings, mappingFile );
        }
        return indexMappings;
    }

    List<IndexMapping> getTemplatesForIndex( final Index index )
    {
        final List<IndexMapping> indexMappings = Lists.newArrayList();
        for ( final String mappingFile : TEMPLATE_FILES )
        {
            doGetMapping( index, indexMappings, mappingFile );
        }
        return indexMappings;
    }

    private void doGetMapping( final Index index, final List<IndexMapping> indexMappings, final String mappingFile )
    {
        try
        {
            final IndexMapping indexMapping = createIndexMapping( index, mappingFile );
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

    private IndexMapping createIndexMapping( final Index index, final String mappingFile )
        throws IOException
    {
        final String filename = mappingFile.substring( PREFIX.length() );

        final String[] parts = filename.split( "-" );
        if ( parts.length < 3 )
        {
            return null;
        }

        final String resourceIndexName = parts[0];
        if ( !index.getName().equals( resourceIndexName ) )
        {
            return null;
        }

        final String indexType = parts[1];
        final String mapping = doGetResourceAsString( mappingFile );

        return new IndexMapping( index, indexType, mapping );
    }

    private String doGetResourceAsString( final String fileName )
        throws IOException
    {
        final URL url = Resources.getResource( getClass(), fileName );
        return Resources.toString( url, Charsets.UTF_8 );
    }


}
