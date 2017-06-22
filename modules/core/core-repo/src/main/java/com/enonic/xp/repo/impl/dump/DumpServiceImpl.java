package com.enonic.xp.repo.impl.dump;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.dump.SystemLoadParams;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.SecurityHelper;
import com.enonic.xp.repo.impl.dump.reader.FileDumpReader;
import com.enonic.xp.repo.impl.dump.writer.FileDumpWriter;
import com.enonic.xp.repository.CreateRepositoryParams;
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
    public SystemDumpResult systemDump( final SystemDumpParams params )
    {
        if ( !SecurityHelper.isAdmin() )
        {
            throw new RepoDumpException( "Only admin role users can dump repositories" );
        }

        this.nodeService.refresh( RefreshMode.ALL );

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
                xpVersion( this.xpVersion ).
                maxVersions( params.getMaxVersions() ).
                maxAge( params.getMaxAge() ).
                listener( params.getListener() ).
                build().
                execute();

            dumpResults.add( result );
        }

        return dumpResults.build();
    }

    @Override
    public SystemLoadResult loadSystemDump( final SystemLoadParams params )
    {
        final SystemLoadResult.Builder results = SystemLoadResult.create();

        if ( !SecurityHelper.isAdmin() )
        {
            throw new RepoDumpException( "Only admin role users can dump repositories" );
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

        return results.build();
    }

    private void initializeSystemRepo( final SystemLoadParams params, final FileDumpReader dumpReader,
                                       final SystemLoadResult.Builder results )
    {
        doLoadRepository( SystemConstants.SYSTEM_REPO.getId(), params, dumpReader, results );

        this.repositoryService.invalidateAll();

        ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
            branch( SystemConstants.BRANCH_SYSTEM ).
            build().runWith( () -> this.nodeService.refresh( RefreshMode.ALL ) );
    }

    private void initializeRepo( final Repository repository )
    {
        if ( !this.repositoryService.isInitialized( repository.getId() ) )
        {
            final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create().
                repositoryId( repository.getId() ).
                repositorySettings( repository.getSettings() ).
                build();

            this.repositoryService.createRepository( createRepositoryParams );
        }
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

    public void setBasePath( final Path basePath )
    {
        this.basePath = basePath;
    }
}
