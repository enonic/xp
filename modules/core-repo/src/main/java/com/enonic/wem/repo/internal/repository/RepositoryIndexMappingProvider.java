package com.enonic.wem.repo.internal.repository;

import com.enonic.wem.api.repository.Repository;

public class RepositoryIndexMappingProvider
    extends AbstractRepositorySettingsProvider
{
    private final static String PREFIX = "/META-INF/index/mapping/";

    private final static String STORAGE_MAPPING_FILE_PATTERN = "-mapping.json";

    private final static String WORKSPACE = "-workspace";

    private final static String VERSION = "-version";

    private final static String SEARCH = "-search";

    public static String getWorkspaceMapping( final Repository repository )
    {
        return doGet( repository, PREFIX + repository.getId().toString() + WORKSPACE + STORAGE_MAPPING_FILE_PATTERN );
    }

    public static String getVersionMapping( final Repository repository )
    {
        return doGet( repository, PREFIX + repository.getId().toString() + VERSION + STORAGE_MAPPING_FILE_PATTERN );
    }

    public static String getSearchMappings( final Repository repository )
    {
        return doGet( repository, PREFIX + repository.getId().toString() + SEARCH + STORAGE_MAPPING_FILE_PATTERN );
    }
}
