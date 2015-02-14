package com.enonic.wem.repo.internal.repository;

import com.enonic.xp.core.repository.RepositoryId;

public class RepositoryStorageSettingsProvider
    extends AbstractRepositorySettingsProvider
{

    public static final String STORAGE_SETTINGS_FILE_PATTERN = "-storage-settings.json";

    private final static String PREFIX = "/META-INF/index/settings/";

    public static String getSettings( final RepositoryId repositoryId )
    {
        return doGet( repositoryId, resolveFileName( repositoryId ) );
    }


    private static String resolveFileName( final RepositoryId repositoryId )
    {
        return ( PREFIX + repositoryId.toString() + STORAGE_SETTINGS_FILE_PATTERN );
    }

}
