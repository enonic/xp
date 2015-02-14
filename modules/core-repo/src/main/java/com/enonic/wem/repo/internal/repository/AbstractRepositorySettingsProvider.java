package com.enonic.wem.repo.internal.repository;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.repository.RepositoryId;
import com.enonic.wem.repo.internal.index.IndexException;

public class AbstractRepositorySettingsProvider
{
    protected static String doGet( final RepositoryId repositoryId, final String fileName )
    {
        try
        {
            final URL url = Resources.getResource( RepositoryStorageSettingsProvider.class, fileName );
            return Resources.toString( url, Charsets.UTF_8 );
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
