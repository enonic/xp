package com.enonic.xp.project;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.security.RoleKeys;

@Beta
public final class ProjectConstants
{
    public static final String PROJECT_REPO_ID_PREFIX = "com.enonic.cms.";

    public static final String PROJECT_REPO_ID_DEFAULT = "default";

    public static final String PROJECT_DATA_SET_NAME = "com-enonic-cms";

    public static final String PROJECT_DISPLAY_NAME_PROPERTY = "displayName";

    public static final String PROJECT_DESCRIPTION_PROPERTY = "description";

    public static final String PROJECT_ICON_PROPERTY = "icon";

    public static final String PROJECT_LAYERS_PROPERTY = "layers";

    public static final String PROJECT_PERMISSIONS_PROPERTY = "permissions";

    public static final String PROJECT_ACCESS_LEVEL_OWNER_PROPERTY = "owner";

    public static final String PROJECT_ACCESS_LEVEL_EXPERT_PROPERTY = "expert";

    public static final String PROJECT_ACCESS_LEVEL_CONTRIBUTOR_PROPERTY = "contributor";

    private static final ProjectPermissions DEFAULT_PROJECT_PERMISSIONS = ProjectPermissions.create().
        addExpert( RoleKeys.CONTENT_MANAGER_EXPERT ).
        addContributor( RoleKeys.CONTENT_MANAGER_APP ).
        build();

    public static final Project DEFAULT_PROJECT = Project.create().
        name( ProjectName.from( ContentConstants.CONTENT_REPO_ID ) ).
        displayName( "Default" ).
        addPermissions( DEFAULT_PROJECT_PERMISSIONS ).
        build();
}
