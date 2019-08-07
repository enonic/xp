package com.enonic.xp.auditlog;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

public class AuditLogConstants
{

    public static final Branch AUDIT_LOG_BRANCH = Branch.create().
        value( "master" ).
        build();

    public static final String AUDIT_LOG_REPO_ID_PREFIX = "system.";

    public static final RepositoryId AUDIT_LOG_REPO_ID = RepositoryId.from( AUDIT_LOG_REPO_ID_PREFIX + "auditlog" );

    public static final Repository AUDIT_LOG_REPO = Repository.create().
        id( AUDIT_LOG_REPO_ID ).
        branches( Branches.from( AUDIT_LOG_BRANCH ) ).
        build();

}
