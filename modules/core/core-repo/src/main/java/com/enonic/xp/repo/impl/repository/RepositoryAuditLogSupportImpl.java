package com.enonic.xp.repo.impl.repository;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.AuditLogUris;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.DeleteBranchParams;
import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(configurationPid = "com.enonic.xp.repo")
public class RepositoryAuditLogSupportImpl
    implements RepositoryAuditLogSupport
{
    private static final String SOURCE = "com.enonic.xp.repo";

    private final AuditLogService auditLogService;

    private volatile boolean isEnabledAuditLog = true;

    @Activate
    public RepositoryAuditLogSupportImpl( @Reference final AuditLogService auditLogService )
    {
        this.auditLogService = auditLogService;
    }

    @Activate
    @Modified
    public void activate( final RepositoryConfig config )
    {
        this.isEnabledAuditLog = config.auditlog_enabled();
    }

    @Override
    public void createRepository( final CreateRepositoryParams params )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "id", params.getRepositoryId().toString() );

            log( "system.repo.create", data, AuditLogUris.from( params.getRepositoryId().toString() ) );
        }
    }

    @Override
    public void deleteRepository( final DeleteRepositoryParams params )
    {
        if ( isEnabledAuditLog )
        {
            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "id", params.getRepositoryId().toString() );

            log( "system.repo.delete", data, AuditLogUris.from( params.getRepositoryId().toString() ) );
        }
    }

    @Override
    public void createBranch( final CreateBranchParams params )
    {
        if ( isEnabledAuditLog )
        {
            final String repositoryId = currentRepositoryId();
            final String branch = params.getBranch().toString();

            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "repositoryId", repositoryId );
            paramsSet.setString( "branch", branch );

            log( "system.repo.branch.create", data, AuditLogUris.from( repositoryId ) );
        }
    }

    @Override
    public void deleteBranch( final DeleteBranchParams params )
    {
        if ( isEnabledAuditLog )
        {
            final String repositoryId = currentRepositoryId();
            final String branch = params.getBranch().toString();

            final PropertyTree data = new PropertyTree();
            final PropertySet paramsSet = data.addSet( "params" );
            paramsSet.setString( "repositoryId", repositoryId );
            paramsSet.setString( "branch", branch );

            log( "system.repo.branch.delete", data, AuditLogUris.from( repositoryId ) );
        }
    }

    private static String currentRepositoryId()
    {
        return ContextAccessor.current().getRepositoryId().toString();
    }

    private void log( final String type, final PropertyTree data, final AuditLogUris uris )
    {
        final Context context = ContextBuilder.copyOf( ContextAccessor.current() ).build();

        final PrincipalKey userPrincipalKey =
            context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser().getKey() : PrincipalKey.ofAnonymous();

        ContextBuilder.from( context )
            .authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.AUDIT_LOG ).build() )
            .build()
            .callWith( () -> auditLogService.log( LogAuditLogParams.create()
                                                      .type( type )
                                                      .source( SOURCE )
                                                      .data( data )
                                                      .objectUris( uris )
                                                      .user( userPrincipalKey )
                                                      .build() ) );
    }
}
