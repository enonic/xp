package com.enonic.wem.repo.internal.repository;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.wem.repo.internal.index.IndexSettings;
import com.enonic.xp.repository.RepositoryId;

class RepositoryStorageSettingsProvider
    extends AbstractRepositorySettingsProvider
{
    private static final String STORAGE_SETTINGS_FILE_PATTERN = "-storage-settings.json";

    private final static String PREFIX = "/META-INF/index/settings/";

    private static final String DEFAULT_STORAGE_SETTINGS_FILE_NAME = "default-storage-settings.json";

    public static IndexSettings getSettings( final RepositoryId repositoryId )
    {
        final JsonNode defaultSettings = doGet( repositoryId, resolveDefaultSettingsFileName() );

        final JsonNode specificSettings = doGet( repositoryId, resolveFileName( repositoryId ) );

        return IndexSettings.from( JsonMergeHelper.merge( defaultSettings, specificSettings ) );
    }

    private static String resolveFileName( final RepositoryId repositoryId )
    {
        return ( PREFIX + repositoryId.toString() + STORAGE_SETTINGS_FILE_PATTERN );
    }

    private static String resolveDefaultSettingsFileName()
    {
        return ( PREFIX + DEFAULT_STORAGE_SETTINGS_FILE_NAME );
    }
}
