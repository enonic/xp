package com.enonic.xp.repo.impl.dump;

import java.util.Map;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public class SystemRepoDefaults
{
    public static final RepositoryId AUDIT_LOG_REPO_ID = RepositoryId.from( "system.auditlog" );

    public static final AccessControlList AUDIT_LOG_REPO_DEFAULT_ACL = AccessControlList.create()
        .add( AccessControlEntry.create().allowAll().principal( RoleKeys.ADMIN ).build() )
        .add( AccessControlEntry.create().allowAll().principal( RoleKeys.AUDIT_LOG ).build() )
        .build();

    public static final IndexPath TIME = IndexPath.from( "time" );

    public static final ChildOrder AUDIT_LOG_REPO_DEFAULT_CHILD_ORDER =
        ChildOrder.create().add( FieldOrderExpr.create( TIME, OrderExpr.Direction.DESC ) ).build();

    public static final RepositoryId SCHEDULER_REPO_ID = RepositoryId.from( "system.scheduler" );

    public static final AccessControlList SCHEDULER_REPO_DEFAULT_ACL =
        AccessControlList.create().add( AccessControlEntry.create().allowAll().principal( RoleKeys.ADMIN ).build() ).build();

    public static final RepositoryId VIRTUAL_APP_REPO_ID = RepositoryId.from( "system.app" );

    public static final AccessControlList VIRTUAL_APP_REPO_DEFAULT_ACL = AccessControlList.create()
        .add( AccessControlEntry.create().allowAll().principal( RoleKeys.ADMIN ).build() )
        .add( AccessControlEntry.create()
                  .principal( RoleKeys.SCHEMA_ADMIN )
                  .allow( Permission.READ, Permission.CREATE, Permission.MODIFY, Permission.DELETE )
                  .build() )
        .build();

    public static final Map<RepositoryId, Data> SYSTEM_REPO_DEFAULTS =
        Map.of( AUDIT_LOG_REPO_ID, new Data( AUDIT_LOG_REPO_DEFAULT_ACL, AUDIT_LOG_REPO_DEFAULT_CHILD_ORDER ), SCHEDULER_REPO_ID,
                new Data( SCHEDULER_REPO_DEFAULT_ACL ), VIRTUAL_APP_REPO_ID, new Data( VIRTUAL_APP_REPO_DEFAULT_ACL ) );

    public record Data(AccessControlList acl, ChildOrder childOrder)
    {
        public Data( AccessControlList acl )
        {
            this( acl, RepositoryConstants.DEFAULT_CHILD_ORDER );
        }
    }
}
