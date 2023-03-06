package com.enonic.xp.archive;

import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public final class ArchiveConstants
{
    public static final String ARCHIVE_ROOT_NAME = "archive";

    public static final NodePath ARCHIVE_ROOT_PATH = new NodePath( NodePath.ROOT, NodeName.from( ARCHIVE_ROOT_NAME ) );

    public static final ChildOrder DEFAULT_ARCHIVE_REPO_ROOT_ORDER = ChildOrder.from( "archivedTime DESC, _name ASC" );

    public static final ChildOrder DEFAULT_CHILD_ORDER =
        ChildOrder.create().add( FieldOrderExpr.create( ContentIndexPath.MODIFIED_TIME, OrderExpr.Direction.DESC ) ).build();

    public static final AccessControlList ARCHIVE_ROOT_DEFAULT_ACL = AccessControlList.create()
        .add( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allowAll().build() )
        .add( AccessControlEntry.create().principal( RoleKeys.CONTENT_MANAGER_ADMIN ).allowAll().build() )
        .add( AccessControlEntry.create()
                  .principal( RoleKeys.CONTENT_MANAGER_APP )
                  .allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE )
                  .build() )
        .build();

    private ArchiveConstants()
    {

    }
}
