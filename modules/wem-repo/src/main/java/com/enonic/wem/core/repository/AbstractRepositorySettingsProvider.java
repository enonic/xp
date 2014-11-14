package com.enonic.wem.core.repository;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.core.index.IndexException;

public class AbstractRepositorySettingsProvider
{
    protected static String doGet( final Repository repository, final String fileName )
    {
        try
        {
            final URL url = Resources.getResource( RepositoryStorageSettingsProvider.class, fileName );
            return Resources.toString( url, Charsets.UTF_8 );
        }
        catch ( IOException e )
        {
            throw new IndexException( "Failed to load settings for repositoryId " + repository.getId() + " from file: " + fileName, e );
        }
        catch ( IllegalArgumentException e )
        {
            throw new IndexException( "Settings for repositoryId " + repository.getId() + " from file: " + fileName + " not found", e );
        }
    }

}
