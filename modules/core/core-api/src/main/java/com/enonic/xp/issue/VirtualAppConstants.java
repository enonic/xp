package com.enonic.xp.issue;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

public final class VirtualAppConstants
{
    public static final RepositoryId VIRTUAL_APP_REPO_ID = RepositoryId.from( "system.app" );

    public static final AccessControlList VIRTUAL_APP_REPO_DEFAULT_ACL =
        AccessControlList.create().add( AccessControlEntry.create().allowAll().principal( RoleKeys.ADMIN ).build() ).build();

    public static final String ADMIN_ROOT_NAME = "admin";

    public static final String WIDGET_ROOT_NAME = "widgets";

    public static final String MIXIN_ROOT_NAME = "mixins";

    public static final String X_DATA_ROOT_NAME = "x-data";

    public static final String SITE_ROOT_NAME = "site";

    public static final String CONTENT_TYPE_ROOT_NAME = "content-types";

    public static final String PART_ROOT_NAME = "parts";

    public static final String PAGE_ROOT_NAME = "pages";

    public static final String LAYOUT_ROOT_NAME = "layouts";

    public static final NodePath VIRTUAL_APP_ROOT_PARENT = NodePath.ROOT;

    public static final Branch VIRTUAL_APP_BRANCH = Branch.from( "master" );

    private VirtualAppConstants()
    {

    }

}
