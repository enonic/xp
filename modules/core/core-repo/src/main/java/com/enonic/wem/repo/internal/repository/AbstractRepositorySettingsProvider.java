package com.enonic.wem.repo.internal.repository;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.repo.internal.index.IndexException;
import com.enonic.xp.repository.RepositoryId;

class AbstractRepositorySettingsProvider
{
    private final static ObjectMapper mapper = create();

    static JsonNode doGet( final RepositoryId repositoryId, final String fileName )
    {
        try
        {
            final URL url = Resources.getResource( RepositoryStorageSettingsProvider.class, fileName );
            return mapper.readTree( Resources.toString( url, Charsets.UTF_8 ) );
        }
        catch ( IOException e )
        {
            throw new IndexException( "Failed to load settings for repositoryId " + repositoryId + " from file: " + fileName, e );
        }
        catch ( IllegalArgumentException e )
        {
            throw new IndexException( "Settings for repositoryId " + repositoryId + " from file: " + fileName + " not found", e );
        }
    }

    private static ObjectMapper create()
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) );
        mapper.disable( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS );
        mapper.disable( SerializationFeature.FAIL_ON_EMPTY_BEANS );
        mapper.enable( MapperFeature.SORT_PROPERTIES_ALPHABETICALLY );
        mapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );
        mapper.setSerializationInclusion( JsonInclude.Include.ALWAYS );
        mapper.registerModule( new JSR310Module() );
        return mapper;
    }

}
