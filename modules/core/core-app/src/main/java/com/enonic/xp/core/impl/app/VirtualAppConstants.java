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

    public static final String FORM_FRAGMENTS_ROOT_NAME = "form-fragments";

    public static final String MIXINS_ROOT_NAME = "mixins";

    public static final String STYLES_ROOT_NAME = "styles";

    public static final String CMS_ROOT_NAME = "cms";

    public static final String CONTENT_TYPE_ROOT_NAME = "content-types";

    public static final String PART_ROOT_NAME = "parts";

    public static final String PAGE_ROOT_NAME = "pages";

    public static final String LAYOUT_ROOT_NAME = "layouts";

    public static final String STYLES_NAME = "image";

    public static final NodePath VIRTUAL_APP_ROOT_PARENT = NodePath.ROOT;

    public static final Branch VIRTUAL_APP_BRANCH = Branch.from( "master" );

    public static final String CMS_RESOURCE_PATH = "/" + CMS_ROOT_NAME + "/" + CMS_ROOT_NAME + ".yml";

    public static final String CMS_DESCRIPTOR_DEFAULT_VALUE = """
        mixin: [ ]
        form: [ ]
        """;

    public static final ByteSource DEFAULT_CMS_RESOURCE_VALUE =
        ByteSource.wrap( CMS_DESCRIPTOR_DEFAULT_VALUE.getBytes( StandardCharsets.UTF_8 ) );

    private VirtualAppConstants()
    {

    }

}
