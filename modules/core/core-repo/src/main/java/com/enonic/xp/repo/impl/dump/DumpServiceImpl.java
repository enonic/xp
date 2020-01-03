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

import com.enonic.xp.app.ApplicationInstallationParams;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.DumpUpgradeResult;
import com.enonic.xp.dump.DumpUpgradeStepResult;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.dump.SystemDumpUpgradeParams;
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
import com.enonic.xp.repo.impl.dump.upgrade.DumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.IndexConfigUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.MissingModelVersionDumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.RepositoryIdDumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.VersionIdDumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.commit.CommitDumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageDumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.htmlarea.HtmlAreaDumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.indexaccesssegments.IndexAccessSegmentsDumpUpgrader;
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
import com.enonic.xp.util.Version;

import static com.google.common.base.Strings.nullToEmpty;

@Component(immediate = true)
@SuppressWarnings("WeakerAccess")
public class DumpServiceImpl
    implements DumpService
{
    private final static Logger LOG = LoggerFactory.getLogger( DumpServiceImpl.class );

    private BlobStore blobStore;

    private NodeService nodeService;

    private RepositoryService repositoryService;

    private ApplicationService applicationService;

    private String xpVersion;

    private Path basePath = Paths.get( HomeDir.get().toString(), "data", "dump" );

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
    public DumpUpgradeResult upgrade( final SystemDumpUpgradeParams params )
    {
        if ( !SecurityHelper.isAdmin() )
        {
            throw new RepoDumpException( "Only admin role users can upgrade dumps" );
        }

        final String dumpName = params.getDumpName();
        if ( nullToEmpty( dumpName ).isBlank() )
        {
            throw new RepoDumpException( "dump name cannot be empty" );
        }

        return doUpgrade( params );
    }

    private DumpUpgradeResult doUpgrade( final SystemDumpUpgradeParams params )
    {
        final DumpUpgradeResult.Builder result = DumpUpgradeResult.create();

        final String dumpName = params.getDumpName();
        Version modelVersion = getDumpModelVersion( dumpName );
        result.initialVersion( modelVersion );
        if ( modelVersion.lessThan( DumpConstants.MODEL_VERSION ) )
        {
            final List<DumpUpgrader> dumpUpgraders = createDumpUpgraders();

            if ( params.getUpgradeListener() != null )
            {
                final long total = dumpUpgraders.stream().
                    map( DumpUpgrader::getModelVersion ).
                    filter( modelVersion::lessThan ).
                    count();

                params.getUpgradeListener().total( total );
            }

            for ( DumpUpgrader dumpUpgrader : dumpUpgraders )
            {
                final Version targetModelVersion = dumpUpgrader.getModelVersion();
                if ( modelVersion.lessThan( targetModelVersion ) )
                {
                    LOG.info( "Running upgrade step [{}]...", dumpUpgrader.getName() );
                    final DumpUpgradeStepResult stepResult = dumpUpgrader.upgrade( dumpName );
                    modelVersion = targetModelVersion;
                    updateDumpModelVersion( dumpName, modelVersion );
                    LOG.info( "Finished upgrade step [{}]: processed: {}, errors: {}, warnings: {}", dumpUpgrader.getName(),
                              stepResult.getProcessed(), stepResult.getErrors(), stepResult.getWarnings() );
                    result.stepResult( stepResult );

                    if ( params.getUpgradeListener() != null )
                    {
                        params.getUpgradeListener().upgraded();
                    }
                }
            }
        }
        result.upgradedVersion( modelVersion );
        return result.build();
    }

    private List<DumpUpgrader> createDumpUpgraders()
    {
        return List.of( new MissingModelVersionDumpUpgrader(), new VersionIdDumpUpgrader( basePath ),
                        new FlattenedPageDumpUpgrader( basePath ), new IndexAccessSegmentsDumpUpgrader( basePath ),
                        new RepositoryIdDumpUpgrader( basePath ), new CommitDumpUpgrader( basePath ), new IndexConfigUpgrader( basePath ),
                        new HtmlAreaDumpUpgrader( basePath ) );
    }

    private Version getDumpModelVersion( final String dumpName )
    {
        Version modelVersion = getDumpMeta( dumpName ).
            getModelVersion();
        if ( modelVersion == null )
        {
            return Version.emptyVersion;
        }
        return modelVersion;
    }

    private void updateDumpModelVersion( final String dumpName, final Version modelVersion )
    {
        final DumpMeta dumpMeta = getDumpMeta( dumpName );
        final DumpMeta updatedDumpMeta = DumpMeta.create( dumpMeta ).
            modelVersion( modelVersion ).
            build();

        new FileDumpWriter( basePath, dumpName, this.blobStore ).
            writeDumpMetaData( updatedDumpMeta );
    }

    private DumpMeta getDumpMeta( final String dumpName )
    {
        final FileDumpReader dumpReader = new FileDumpReader( basePath, dumpName, null );
        return dumpReader.getDumpMeta();
    }

    @Override
    public SystemDumpResult dump( final SystemDumpParams params )
    {
        if ( !SecurityHelper.isAdmin() )
        {
            throw new RepoDumpException( "Only admin role users can dump repositories" );
        }

        final FileDumpWriter writer = new FileDumpWriter( basePath, params.getDumpName(), blobStore );

        final Repositories repositories = this.repositoryService.list();

        if ( params.getListener() != null )
        {
            final long branchesCount = repositories.
                stream().
                flatMap( repository -> repository.getBranches().stream() ).
                count();

            params.getListener().totalBranches( branchesCount );
        }

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
        writer.writeDumpMetaData( DumpMeta.create().
            xpVersion( this.xpVersion ).
            modelVersion( DumpConstants.MODEL_VERSION ).
            timestamp( Instant.now() ).
            systemDumpResult( systemDumpResult ).build() );

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

        final Version modelVersion = getDumpModelVersion( params.getDumpName() );
        if ( modelVersion.getMajor() < DumpConstants.MODEL_VERSION.getMajor() )
        {
            if ( params.isUpgrade() )
            {
                final SystemDumpUpgradeParams dumpUpgradeParams = SystemDumpUpgradeParams.create().
                    dumpName( params.getDumpName() ).
                    build();
                doUpgrade( dumpUpgradeParams );
            }
            else
            {
                throw new RepoLoadException(
                    "Cannot load system-dump; major model version previous to the current version; upgrade the system-dump" );
            }
        }

        final FileDumpReader dumpReader = new FileDumpReader( basePath, params.getDumpName(), params.getListener() );
        final RepositoryIds dumpRepositories = dumpReader.getRepositories();

        if ( !dumpRepositories.contains( SystemConstants.SYSTEM_REPO.getId() ) )
        {
            throw new SystemDumpException( "Cannot load system-dump; dump does not contain system repository" );
        }

        uninstallNonSystemApplications();

        if ( params.getListener() != null )
        {
            final long branchesCount = dumpReader.getRepositories().
                stream().
                flatMap( repositoryId -> dumpReader.getBranches( repositoryId ).stream() ).
                count();

            params.getListener().totalBranches( branchesCount );
        }

        doDeleteRepositories();

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

    private void uninstallNonSystemApplications()
    {
        LOG.info( "Uninstall global applications" );
        NodeHelper.runAsAdmin( () -> {
            final Applications installedApplications = applicationService.getInstalledApplications();
            installedApplications.forEach( installedApplication -> {
                if ( !installedApplication.isSystem() )
                {
                    final ApplicationKey applicationKey = installedApplication.getKey();
                    if (applicationService.isLocalApplication( applicationKey )) {
                        applicationService.publishUninstalledEvent(applicationKey);
                    } else {
                        applicationService.uninstallApplication( installedApplication.getKey(), true );
                    }
                }
            } );
        } );
    }

    private void initApplications()
    {
        LOG.info( "Install global applications" );
        final ApplicationInstallationParams params = ApplicationInstallationParams.create().
            build();
        NodeHelper.runAsAdmin( () -> applicationService.installAllStoredApplications(params) );
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

    private void doDeleteRepositories()
    {
        repositoryService.list().stream().
            filter( ( repo ) -> !repo.getId().equals( SystemConstants.SYSTEM_REPO.getId() ) ).
            forEach( ( repo ) -> doDeleteRepository( repo.getId() ) );
    }

    private void doDeleteRepository( final RepositoryId repositoryId )
    {
        LOG.info( "Deleting repository [" + repositoryId + "]" );
        this.repositoryService.deleteRepository( DeleteRepositoryParams.from( repositoryId ) );
    }

    private void initializeRepo( final Repository repository )
    {
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
