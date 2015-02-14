package com.enonic.wem.repo.internal.repository;

import com.enonic.xp.repository.RepositoryId;

public class RepositoryIndexMappingProvider
    extends AbstractRepositorySettingsProvider
{
    private final static String PREFIX = "/META-INF/index/mapping/";

    private final static String STORAGE_MAPPING_FILE_PATTERN = "-mapping.json";

    private final static String BRANCH = "-branch";

    private final static String VERSION = "-version";

    private final static String NODE = "-node";

    private final static String SEARCH = "-search";

    public static String getBranchMapping( final RepositoryId repositoryId )
    {
        return doGet( repositoryId, PREFIX + repositoryId.toString() + BRANCH + STORAGE_MAPPING_FILE_PATTERN );
    }

    public static String getVersionMapping( final RepositoryId repositoryId )
    {
        return doGet( repositoryId, PREFIX + repositoryId.toString() + VERSION + STORAGE_MAPPING_FILE_PATTERN );
    }

    public static String getNodeMapping( final RepositoryId repositoryId )
    {
        return doGet( repositoryId, PREFIX + repositoryId.toString() + NODE + STORAGE_MAPPING_FILE_PATTERN );
    }


    public static String getSearchMappings( final RepositoryId repositoryId )
    {
        return doGet( repositoryId, PREFIX + repositoryId.toString() + SEARCH + STORAGE_MAPPING_FILE_PATTERN );
    }
}
