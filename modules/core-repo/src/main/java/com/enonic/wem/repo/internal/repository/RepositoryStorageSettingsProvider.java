package com.enonic.wem.repo.internal.repository;

import com.enonic.wem.api.repository.Repository;

public class RepositoryStorageSettingsProvider
    extends AbstractRepositorySettingsProvider
{

    private final static String PREFIX = "/META-INF/index/settings/";

    public static final String STORAGE_SETTINGS_FILE_PATTERN = "-storage-settings.json";


    public static String getSettings( final Repository repository )
    {
        return doGet( repository, resolveFileName( repository ) );
    }


    private static String resolveFileName( final Repository repository )
    {
        return ( PREFIX + repository.getId().toString() + STORAGE_SETTINGS_FILE_PATTERN );
    }

}
