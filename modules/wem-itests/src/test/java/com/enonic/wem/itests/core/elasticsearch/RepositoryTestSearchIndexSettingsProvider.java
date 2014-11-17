package com.enonic.wem.itests.core.elasticsearch;

import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.repo.internal.repository.AbstractRepositorySettingsProvider;

public class RepositoryTestSearchIndexSettingsProvider
    extends AbstractRepositorySettingsProvider
{
    private final static String PREFIX = "/META-INF/index/settings/";

    public static final String SEARCH_SETTINGS_FILE_PATTERN = "search-test-settings.json";


    public static String getSettings( final Repository repository )
    {
        return doGet( repository, resolveFileName( repository ) );
    }

    private static String resolveFileName( final Repository repository )
    {
        return ( PREFIX + SEARCH_SETTINGS_FILE_PATTERN );
    }


}
