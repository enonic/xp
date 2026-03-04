package com.enonic.xp.project;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.repository.RepositoryConstants;

@PublicApi
public final class ProjectConstants
{
    public static final String PROJECT_REPO_ID_PREFIX = "com.enonic.cms.";

    public static final String PROJECT_NAME_PREFIX = "cms.project.";

    public static final String PROJECT_DATA_SET_NAME = "com-enonic-cms";

    public static final String PROJECT_DISPLAY_NAME_PROPERTY = "displayName";

    public static final String PROJECT_DESCRIPTION_PROPERTY = "description";

    public static final String PROJECT_ICON_PROPERTY = "icon";

    public static final String PROJECT_PARENTS_PROPERTY = "parents";

    public static final int PROJECT_NAME_MAX_LENGTH = RepositoryConstants.REPOSITORY_ID_MAX_LENGTH - PROJECT_REPO_ID_PREFIX.length();

    private ProjectConstants()
    {
    }
}
