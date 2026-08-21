package com.enonic.xp.core.impl.schema;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.util.BinaryReference;

public final class NamespaceConstants
{
    public static final RepositoryId NAMESPACE_APP_REPO_ID = RepositoryId.from( "system.app" );

    public static final AccessControlList NAMESPACE_APP_REPO_DEFAULT_ACL = AccessControlList.create()
        .add( AccessControlEntry.create().allowAll().principal( RoleKeys.ADMIN ).build() )
        .add( AccessControlEntry.create()
                  .principal( RoleKeys.SCHEMA_ADMIN )
                  .allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE )
                  .build() )
        .build();

    public static final String FORM_FRAGMENTS_ROOT_NAME = "form-fragments";

    public static final String MIXINS_ROOT_NAME = "mixins";

    public static final String STYLE_ROOT_NAME = "style";

    public static final String CMS_ROOT_NAME = "cms";

    public static final String CONTENT_TYPE_ROOT_NAME = "content-types";

    public static final String PART_ROOT_NAME = "parts";

    public static final String PAGE_ROOT_NAME = "pages";

    public static final String LAYOUT_ROOT_NAME = "layouts";

    public static final String MACROS_ROOT_NAME = "macros";

    public static final String I18N_ROOT_NAME = "i18n";

    public static final String PHRASES_ROOT_NAME = "phrases";

    public static final String STYLE_NAME = "style";

    public static final BinaryReference ICON_BINARY_REFERENCE = BinaryReference.from( "icon" );

    public static final NodePath NAMESPACE_APP_ROOT_PARENT = NodePath.ROOT;

    public static final Branch NAMESPACE_APP_BRANCH = Branch.from( "master" );

    private NamespaceConstants()
    {

    }

}
