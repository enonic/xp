package com.enonic.xp.core.impl.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.init.ExternalInitializer;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

class ApplicationRepoInitializer
    extends ExternalInitializer
{
    private final NodeService nodeService;

    private static final PrincipalKey SUPER_USER = PrincipalKey.ofSuperUser();

    private static final Logger LOG = LoggerFactory.getLogger( ApplicationRepoInitializer.class );

    private ApplicationRepoInitializer( final Builder builder )
    {
        super( builder );
        this.nodeService = builder.nodeService;
    }

    @Override
    public final void doInitialize()
    {
        createAdminContext().
            runWith( () -> initApplicationFolder() );
    }

    @Override
    public boolean isInitialized()
    {
        return createAdminContext().
            callWith( () -> this.nodeService.getByPath( ApplicationRepoServiceImpl.APPLICATION_PATH ) != null );
    }

    @Override
    protected String getInitializationSubject()
    {
        return "System-repo [applications] layout";
    }

    private void initApplicationFolder()
    {
        final NodePath applicationsRootPath = ApplicationRepoServiceImpl.APPLICATION_PATH;
        LOG.info( "Initializing [" + applicationsRootPath.toString() + "] folder" );

        nodeService.create( CreateNodeParams.create().
            parent( applicationsRootPath.getParentPath() ).
            name( applicationsRootPath.getLastElement().toString() ).
            inheritPermissions( true ).
            build() );
    }

    private Context createAdminContext()
    {
        final User admin = User.create().key( SUPER_USER ).login( SUPER_USER.getId() ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();
        return ContextBuilder.from( ApplicationConstants.CONTEXT_APPLICATIONS ).authInfo( authInfo ).build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends ExternalInitializer.Builder<Builder>
    {
        private NodeService nodeService;

        public Builder setNodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        @Override
        protected void validate()
        {
            super.validate();
            Preconditions.checkNotNull( nodeService );
        }

        public ApplicationRepoInitializer build()
        {
            validate();
            return new ApplicationRepoInitializer( this );
        }
    }
}
