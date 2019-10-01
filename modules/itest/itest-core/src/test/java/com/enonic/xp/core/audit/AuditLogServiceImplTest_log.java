package com.enonic.xp.core.audit;

import org.junit.jupiter.api.Test;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.audit.AuditLogConstants;
import com.enonic.xp.core.impl.audit.AuditLogContext;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.*;

public class AuditLogServiceImplTest_log
    extends AbstractAuditLogServiceTest
{
    @Test
    public void log_anonymous()
    {
        final Context context = ContextBuilder.create().
            repositoryId( AuditLogConstants.AUDIT_LOG_REPO_ID ).
            branch( AuditLogConstants.AUDIT_LOG_BRANCH ).
            authInfo( AuthenticationInfo.create().
                principals( PrincipalKey.ofAnonymous() ).
                user( User.ANONYMOUS ).
                build() ).build();

        assertThrows( NodeAccessException.class, () -> context.runWith( () -> {
            LogAuditLogParams params = LogAuditLogParams.create().type( "test" ).build();
            auditLogService.log( params );
        } ) );
    }

    @Test
    public void get()
    {
        final LogAuditLogParams params = LogAuditLogParams.create().type( "test" ).build();

        final AuditLog log1 = AuditLogContext.createAdminContext().callWith( () -> auditLogService.log( params ) );
        final AuditLog log2 = AuditLogContext.createAdminContext().callWith( () -> auditLogService.get( log1.getId() ) );

        assertEquals( log1, log2 );
    }

}
