package com.enonic.wem.repo.internal.elasticsearch;

import com.enonic.xp.core.repository.Repository;
import com.enonic.wem.repo.internal.repository.AbstractRepositorySettingsProvider;

class RepositoryTestSearchIndexSettingsProvider
    extends AbstractRepositorySettingsProvider
{
    private final static String PREFIX = "/META-INF/index/settings/";

    private static final String SEARCH_SETTINGS_FILE_PATTERN = "search-test-settings.json";


    public static String getSettings( final Repository repository )
    {
        return doGet( repository.getId(), resolveFileName( repository ) );
    }

    private static String resolveFileName( final Repository repository )
    {
        return ( PREFIX + SEARCH_SETTINGS_FILE_PATTERN );
    }


}
