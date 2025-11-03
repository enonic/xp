package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

@PublicApi
public final class ContentConstants
{
    public static final String DOCUMENT_INDEX_DEFAULT_ANALYZER = "document_index_default";

    public static final Branch BRANCH_DRAFT = Branch.create().value( "draft" ).build();

    public static final Branch BRANCH_MASTER = Branch.create().value( "master" ).build();

    public static final String CONTENT_REPO_ID_PREFIX = ProjectConstants.PROJECT_REPO_ID_PREFIX;

    public static final AccessControlList CONTENT_REPO_DEFAULT_ACL = AccessControlList.create()
        .add( AccessControlEntry.create().allowAll().principal( RoleKeys.ADMIN ).build() )
        .add( AccessControlEntry.create().allow( Permission.READ ).principal( RoleKeys.CONTENT_MANAGER_ADMIN ).build() )
        .build();

    public static final String CONTENT_ROOT_NAME = "content";

    public static final NodePath CONTENT_ROOT_PATH = new NodePath( NodePath.ROOT, NodeName.from( CONTENT_ROOT_NAME ) );

    public static final NodeType CONTENT_NODE_COLLECTION = NodeType.from( "content" );

    public static final ChildOrder DEFAULT_CONTENT_REPO_ROOT_ORDER = ChildOrder.name();

    public static final ChildOrder DEFAULT_CHILD_ORDER =
        ChildOrder.create().add( FieldOrderExpr.create( ContentIndexPath.MODIFIED_TIME, OrderExpr.Direction.DESC ) ).build();

    public static final String PUBLISH_COMMIT_PREFIX = "COM_ENONIC_XP_CONTENT_PUBLISH";

    public static final String UNPUBLISH_COMMIT_PREFIX = "COM_ENONIC_XP_CONTENT_UNPUBLISH";

    public static final String RESTORE_COMMIT_PREFIX = "COM_ENONIC_XP_CONTENT_RESTORE";

    public static final String ARCHIVE_COMMIT_PREFIX = "COM_ENONIC_XP_CONTENT_ARCHIVE";

    public static final String APPLY_PERMISSIONS_COMMIT_PREFIX = "COM_ENONIC_XP_CONTENT_APPLY_PERMISSIONS";

    public static final String PUBLISH_COMMIT_PREFIX_DELIMITER = " ";

    public static final String ARCHIVE_COMMIT_PREFIX_DELIMITER = " ";

    public static final String CONTENT_ROOT_PATH_ATTRIBUTE = "contentRootPath";

    private static final PrincipalKey CONTENT_SUPER_USER_KEY = PrincipalKey.ofUser( IdProviderKey.system(), "content-su" );

    private static final User CONTENT_SUPER_USER = User.create().key( CONTENT_SUPER_USER_KEY ).login( "content" ).build();

    public static final AuthenticationInfo CONTENT_SU_AUTH_INFO =
        AuthenticationInfo.create().principals( CONTENT_SUPER_USER_KEY, RoleKeys.ADMIN ).user( CONTENT_SUPER_USER ).build();

    private ContentConstants()
    {

    }

}
