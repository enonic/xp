package com.enonic.xp.project;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.ContentConstants;

@Beta
public final class ProjectConstants
{
    public static final String PROJECT_REPO_ID_PREFIX = "com.enonic.cms.";

    public static final String PROJECT_NAME_PREFIX = "cms.project.";

    public static final String PROJECT_REPO_ID_DEFAULT = "default";

    public static final String PROJECT_DATA_SET_NAME = "com-enonic-cms";

    public static final String PROJECT_DISPLAY_NAME_PROPERTY = "displayName";

    public static final String PROJECT_TIMEZONE_PROPERTY = "timeZone";

    public static final String PROJECT_DESCRIPTION_PROPERTY = "description";

    public static final String PROJECT_ICON_PROPERTY = "icon";

    public static final String PROJECT_PARENTS_PROPERTY = "parents";

    public static final String PROJECT_SITE_CONFIG_PROPERTY = "siteConfig";

    public static final ProjectName DEFAULT_PROJECT_NAME = ProjectName.from( ContentConstants.CONTENT_REPO_ID );

    public static final Project DEFAULT_PROJECT = Project.create().name( DEFAULT_PROJECT_NAME ).displayName( "Default" ).build();
}
