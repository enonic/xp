package com.enonic.xp.repo.impl.dump;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.dump.SystemLoadParams;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.SecurityHelper;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.reader.FileDumpReader;
import com.enonic.xp.repo.impl.dump.writer.FileDumpWriter;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repo.impl.node.executor.BatchedGetChildrenExecutor;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.SystemConstants;

@Component(immediate = true)
@SuppressWarnings("WeakerAccess")
public class DumpServiceImpl
    implements DumpService
{
    private BlobStore blobStore;

    private NodeService nodeService;

    private RepositoryService repositoryService;

    private ApplicationService applicationService;

    private String xpVersion;

    private Path basePath = Paths.get( HomeDir.get().toString(), "data", "dump" );

    private final static Logger LOG = LoggerFactory.getLogger( DumpServiceImpl.class );

    @SuppressWarnings("unused")
    @Activate
    public void activate( final ComponentContext context )
    {
        xpVersion = context.getBundleContext().
            getBundle().
            getVersion().
            toString();
    }

    @Override
    public SystemDumpResult dump( final SystemDumpParams params )
    {
        if ( !SecurityHelper.isAdmin() )
        {
            throw new RepoDumpException( "Only admin role users can dump repositories" );
        }

        final FileDumpWriter writer = FileDumpWriter.create().
            basePath( basePath ).
            dumpName( params.getDumpName() ).
            blobStore( this.blobStore ).
            build();

        final Repositories repositories = this.repositoryService.list();

        final SystemDumpResult.Builder dumpResults = SystemDumpResult.create();

        for ( final Repository repository : repositories )
        {
            final RepoDumpResult result = RepoDumper.create().
                writer( writer ).
                includeVersions( params.isIncludeVersions() ).
                includeBinaries( params.isIncludeBinaries() ).
                nodeService( this.nodeService ).
                repositoryService( this.repositoryService ).
                repositoryId( repository.getId() ).
                maxVersions( params.getMaxVersions() ).
                maxAge( params.getMaxAge() ).
                listener( params.getListener() ).
                build().
                execute();

            dumpResults.add( result );
        }

        final SystemDumpResult systemDumpResult = dumpResults.build();
        writer.writeDumpMetaData( new DumpMeta( this.xpVersion, Instant.now(), systemDumpResult ) );   
        
        return systemDumpResult;
    }

    @Override
    public SystemLoadResult load( final SystemLoadParams params )
    {
        final SystemLoadResult.Builder results = SystemLoadResult.create();

        if ( !SecurityHelper.isAdmin() )
        {
            throw new RepoLoadException( "Only admin role users can load repositories" );
        }

        final FileDumpReader dumpReader = new FileDumpReader( basePath, params.getDumpName(), params.getListener() );

        final RepositoryIds dumpRepositories = dumpReader.getRepositories();

        if ( !dumpRepositories.contains( SystemConstants.SYSTEM_REPO.getId() ) )
        {
            throw new SystemDumpException( "Cannot load system-dump; dump does not contain system repository" );
        }

        initializeSystemRepo( params, dumpReader, results );

        final List<Repository> repositoriesToLoad = repositoryService.list().stream().
            filter( ( repo ) -> !repo.getId().equals( SystemConstants.SYSTEM_REPO.getId() ) ).
            collect( Collectors.toList() );

        for ( Repository repository : repositoriesToLoad )
        {
            initializeRepo( repository );
            doLoadRepository( repository.getId(), params, dumpReader, results );
        }

        initApplications();

        return results.build();
    }

    private void initApplications()
    {
        LOG.info( "Install applications" );
        NodeHelper.runAsAdmin( () -> applicationService.installAllStoredApplications() );
    }

    private void initializeSystemRepo( final SystemLoadParams params, final FileDumpReader dumpReader,
                                       final SystemLoadResult.Builder results )
    {
        final Context systemContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
            branch( SystemConstants.BRANCH_SYSTEM ).
            build();

        systemContext.runWith( () -> this.nodeService.refresh( RefreshMode.ALL ) );

        doDeleteAllNodes( systemContext );

        doLoadRepository( SystemConstants.SYSTEM_REPO.getId(), params, dumpReader, results );

        this.repositoryService.invalidateAll();

        systemContext.runWith( () -> this.nodeService.refresh( RefreshMode.ALL ) );
    }

    private void doDeleteAllNodes( final Context context )
    {
        context.runWith( () -> {
            final BatchedGetChildrenExecutor executor = BatchedGetChildrenExecutor.create().parentId( Node.ROOT_UUID ).
                nodeService( this.nodeService ).
                recursive( false ).
                build();

            while ( executor.hasMore() )
            {
                final NodeIds children = executor.execute();
                children.forEach( ( child ) -> this.nodeService.deleteById( child ) );
            }
        } );
    }

    private void initializeRepo( final Repository repository )
    {
        if ( this.repositoryService.isInitialized( repository.getId() ) )
        {
            LOG.info( "Deleting repository [" + repository.getId() + "] before loading" );
            this.repositoryService.deleteRepository( DeleteRepositoryParams.from( repository.getId() ) );
        }

        final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create().
            repositoryId( repository.getId() ).
            repositorySettings( repository.getSettings() ).
            build();

        this.repositoryService.createRepository( createRepositoryParams );
    }

    private void doLoadRepository( final RepositoryId repositoryId, final SystemLoadParams params, final FileDumpReader dumpReader,
                                   final SystemLoadResult.Builder builder )
    {
        LOG.info( "Loading repository [" + repositoryId + "]" );

        builder.add( RepoLoader.create().
            reader( dumpReader ).
            nodeService( this.nodeService ).
            blobStore( this.blobStore ).
            includeVersions( params.isIncludeVersions() ).
            repositoryService( this.repositoryService ).
            repositoryId( repositoryId ).
            build().
            execute() );
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Reference
    public void setBlobStore( final BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    public void setBasePath( final Path basePath )
    {
        this.basePath = basePath;
    }
}
