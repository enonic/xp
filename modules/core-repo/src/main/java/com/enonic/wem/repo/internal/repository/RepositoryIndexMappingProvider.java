package com.enonic.wem.repo.internal.repository;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.repo.internal.index.IndexException;
import com.enonic.xp.repository.RepositoryId;

public class RepositoryIndexMappingProvider
{
    private final static String PREFIX = "/META-INF/index/mapping/";

    private final static String STORAGE_MAPPING_FILE_PATTERN = "-mapping.json";

    private final static String BRANCH = "-branch";

    private final static String VERSION = "-version";

    private final static String SEARCH = "-search";

    public static String getBranchMapping( final RepositoryId repositoryId )
    {
        return doGet( repositoryId, PREFIX + repositoryId.toString() + BRANCH + STORAGE_MAPPING_FILE_PATTERN );
    }

    public static String getVersionMapping( final RepositoryId repositoryId )
    {
        return doGet( repositoryId, PREFIX + repositoryId.toString() + VERSION + STORAGE_MAPPING_FILE_PATTERN );
    }

    public static String getSearchMappings( final RepositoryId repositoryId )
    {
        return doGet( repositoryId, PREFIX + repositoryId.toString() + SEARCH + STORAGE_MAPPING_FILE_PATTERN );
    }

    private static String doGet( final RepositoryId repositoryId, final String fileName )
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
