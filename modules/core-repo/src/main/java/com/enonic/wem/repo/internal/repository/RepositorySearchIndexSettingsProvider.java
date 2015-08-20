package com.enonic.wem.repo.internal.repository;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.wem.repo.internal.index.IndexSettings;
import com.enonic.xp.repository.RepositoryId;

public class RepositorySearchIndexSettingsProvider
    extends AbstractRepositorySettingsProvider
{
    private final static String PREFIX = "/META-INF/index/settings/";

    private static final String SEARCH_SETTINGS_FILE_PATTERN = "-search-settings.json";

    private static final String DEFAULT_SEARCH_SETTINGS_FILE_NAME = "default-search-settings.json";

    public static IndexSettings getSettings( final RepositoryId repositoryId )
    {
        final JsonNode defaultSettings = doGet( repositoryId, resolveDefaultSettingsFileName() );

        final JsonNode specificSettings = doGet( repositoryId, resolveFileName( repositoryId ) );

        return IndexSettings.from( JsonMergeHelper.merge( defaultSettings, specificSettings ) );
    }

    private static String resolveFileName( final RepositoryId repositoryId )
    {
        return ( PREFIX + repositoryId.toString() + SEARCH_SETTINGS_FILE_PATTERN );
    }

    private static String resolveDefaultSettingsFileName()
    {
        return ( PREFIX + DEFAULT_SEARCH_SETTINGS_FILE_NAME );
    }

}
