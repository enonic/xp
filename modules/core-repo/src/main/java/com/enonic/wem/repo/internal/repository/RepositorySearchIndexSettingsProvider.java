package com.enonic.wem.repo.internal.repository;

import com.enonic.wem.api.repository.RepositoryId;

public class RepositorySearchIndexSettingsProvider
    extends AbstractRepositorySettingsProvider
{
    private final static String PREFIX = "/META-INF/index/settings/";

    private static final String SEARCH_SETTINGS_FILE_PATTERN = "-search-settings.json";


    public static String getSettings( final RepositoryId repositoryId )
    {
        return doGet( repositoryId, resolveFileName( repositoryId ) );
    }

    private static String resolveFileName( final RepositoryId repositoryId )
    {
        return ( PREFIX + repositoryId.toString() + SEARCH_SETTINGS_FILE_PATTERN );
    }


}
