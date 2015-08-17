package com.enonic.wem.repo.internal.repository;

import com.enonic.wem.repo.internal.index.IndexSettings;
import com.enonic.xp.repository.RepositoryId;

public class RepositoryStorageSettingsProvider
    extends AbstractRepositorySettingsProvider
{
    public static final String STORAGE_SETTINGS_FILE_PATTERN = "-storage-settings.json";

    private final static String PREFIX = "/META-INF/index/settings/";

    public static IndexSettings getSettings( final RepositoryId repositoryId )
    {
        return IndexSettings.from( doGet( repositoryId, resolveFileName( repositoryId ) ) );
    }

    private static String resolveFileName( final RepositoryId repositoryId )
    {
        return ( PREFIX + repositoryId.toString() + STORAGE_SETTINGS_FILE_PATTERN );
    }

}
