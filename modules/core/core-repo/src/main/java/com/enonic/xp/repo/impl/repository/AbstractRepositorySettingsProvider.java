package com.enonic.xp.repo.impl.repository;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.repo.impl.index.IndexException;
import com.enonic.xp.repository.RepositoryId;

class AbstractRepositorySettingsProvider
{
    private final static ObjectMapper mapper = ObjectMapperHelper.create();

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
}
