package com.enonic.xp.core.impl.app;

import java.nio.charset.StandardCharsets;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public final class VirtualAppConstants
{
    public static final RepositoryId VIRTUAL_APP_REPO_ID = RepositoryId.from( "system.app" );

    public static final AccessControlList VIRTUAL_APP_REPO_DEFAULT_ACL = AccessControlList.create()
        .add( AccessControlEntry.create().allowAll().principal( RoleKeys.ADMIN ).build() )
        .add( AccessControlEntry.create()
                  .principal( RoleKeys.SCHEMA_ADMIN )
                  .allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE )
                  .build() )
        .build();

    public static final String MIXIN_ROOT_NAME = "form-fragments";

    public static final String X_DATA_ROOT_NAME = "x-data";

    public static final String SITE_ROOT_NAME = "site";

    public static final String CONTENT_TYPE_ROOT_NAME = "content-types";

    public static final String PART_ROOT_NAME = "parts";

    public static final String PAGE_ROOT_NAME = "pages";

    public static final String LAYOUT_ROOT_NAME = "layouts";

    public static final String STYLES_NAME = "styles";

    public static final NodePath VIRTUAL_APP_ROOT_PARENT = NodePath.ROOT;

    public static final Branch VIRTUAL_APP_BRANCH = Branch.from( "master" );

    public static final String SITE_RESOURCE_PATH = "/" + SITE_ROOT_NAME + "/" + SITE_ROOT_NAME + ".xml";

    public static final ByteSource DEFAULT_SITE_RESOURCE_VALUE = ByteSource.wrap( "<site></site>".getBytes( StandardCharsets.UTF_8 ) );

    private VirtualAppConstants()
    {

    }

}
