package com.enonic.xp.core.impl.project.init;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.init.ExternalInitializer;
import com.enonic.xp.repository.BranchAlreadyExistsException;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryAlreadyExistsException;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

public class ContentRepoInitializer
    extends ExternalInitializer
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentRepoInitializer.class );

    private final RepositoryService repositoryService;

    private final PropertyTree repositoryData;

    protected final RepositoryId repositoryId;

    private ContentRepoInitializer( Builder builder )
    {
        super( builder );
        this.repositoryService = Objects.requireNonNull( builder.repositoryService );
        this.repositoryData = Objects.requireNonNull( builder.repositoryData );
        this.repositoryId = Objects.requireNonNull( builder.repositoryId );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void doInitialize()
    {
        createAdminContext().runWith( () -> {
            initializeRepository();
            createDraftBranch();
        } );
    }

    @Override
    protected boolean isInitialized()
    {
        return createAdminContext().callWith( () -> repositoryService.isInitialized( repositoryId ) &&
            repositoryService.get( repositoryId ).getBranches().contains( ContentConstants.BRANCH_DRAFT ) );
    }

    @Override
    protected String getInitializationSubject()
    {
        return repositoryId + " repo";
    }

    private void createDraftBranch()
    {
        try
        {
            this.repositoryService.createBranch( CreateBranchParams.from( ContentConstants.BRANCH_DRAFT ) );
        }
        catch ( BranchAlreadyExistsException e )
        {
            LOG.debug( "Skip content repository branch init as it already exists", e );
        }
    }

    private void initializeRepository()
    {
        try
        {
            this.repositoryService.createRepository( CreateRepositoryParams.create()
                                                         .repositoryId( repositoryId )
                                                         .data( repositoryData )
                                                         .rootPermissions( ContentConstants.CONTENT_REPO_DEFAULT_ACL )
                                                         .rootChildOrder( ContentConstants.DEFAULT_CONTENT_REPO_ROOT_ORDER )
                                                         .build() );
        }
        catch ( RepositoryAlreadyExistsException e )
        {
            LOG.debug( "Skip content repository init as it already exists", e );
        }
    }

    private Context createAdminContext()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .branch( RepositoryConstants.MASTER_BRANCH )
            .repositoryId( repositoryId )
            .authInfo( RepoDependentInitializer.SUPER_USER_AUTH )
            .build();
    }

    public static class Builder
        extends ExternalInitializer.Builder<Builder>
    {
        private RepositoryService repositoryService;

        private PropertyTree repositoryData;

        private RepositoryId repositoryId;

        public Builder repositoryService( final RepositoryService repositoryService )
        {
            this.repositoryService = repositoryService;
            return this;
        }

        public Builder repositoryData( final PropertyTree repositoryData )
        {
            this.repositoryData = repositoryData;
            return this;
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public ContentRepoInitializer build()
        {
            return new ContentRepoInitializer( this );
        }
    }
}
