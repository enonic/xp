package com.enonic.wem.core.repository;

import com.enonic.wem.api.repository.Repository;

public class RepositorySearchIndexSettingsProvider
    extends AbstractRepositorySettingsProvider
{
    private final static String PREFIX = "/META-INF/index/settings/";

    public static final String SEARCH_SETTINGS_FILE_PATTERN = "-search-settings.json";


    public static String getSettings( final Repository repository )
    {
        return doGet( repository, resolveFileName( repository ) );
    }

    private static String resolveFileName( final Repository repository )
    {
        return ( PREFIX + repository.getId().toString() + SEARCH_SETTINGS_FILE_PATTERN );
    }


}
