package com.enonic.xp.core.impl.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.init.ExternalInitializer;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.PrincipalKey;

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
        ApplicationHelper.runAsAdmin( this::initApplicationFolder );
    }

    @Override
    public boolean isInitialized()
    {
        return ApplicationHelper.runAsAdmin( () -> this.nodeService.getByPath( ApplicationRepoServiceImpl.APPLICATION_PATH ) != null );
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
