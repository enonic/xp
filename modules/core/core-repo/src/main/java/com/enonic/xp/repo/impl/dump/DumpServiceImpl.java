package com.enonic.xp.repo.impl.dump;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.DumpUpgradeResult;
import com.enonic.xp.dump.DumpUpgradeStepResult;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.dump.SystemDumpUpgradeParams;
import com.enonic.xp.dump.SystemLoadParams;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.RepositoryEvents;
import com.enonic.xp.repo.impl.SecurityHelper;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.reader.DumpReader;
import com.enonic.xp.repo.impl.dump.reader.FileDumpReader;
import com.enonic.xp.repo.impl.dump.reader.ZipDumpReader;
import com.enonic.xp.repo.impl.dump.upgrade.DumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.IndexConfigUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.MissingModelVersionDumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.RepositoryIdDumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.VersionIdDumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.commit.CommitDumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageDumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.htmlarea.HtmlAreaDumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.indexaccesssegments.IndexAccessSegmentsDumpUpgrader;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repo.impl.dump.writer.FileDumpWriter;
import com.enonic.xp.repo.impl.dump.writer.ZipDumpWriter;
import com.enonic.xp.repo.impl.repository.RepositoryEntryService;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.util.Version;

import static com.google.common.base.Strings.nullToEmpty;

@Component(immediate = true)
@SuppressWarnings("WeakerAccess")
public class DumpServiceImpl
    implements DumpService
{
    private static final Logger LOG = LoggerFactory.getLogger( DumpServiceImpl.class );

    private static final List<RepositoryId> SYSTEM_REPO_IDS =
        List.of( RepositoryId.from( "system.auditlog" ), RepositoryId.from( "system.scheduler" ) );

    private BlobStore blobStore;

    private NodeService nodeService;

    private RepositoryEntryService repositoryEntryService;

    private NodeRepositoryService nodeRepositoryService;

    private NodeStorageService nodeStorageService;

    private final EventPublisher eventPublisher;

    private final String xpVersion;

    private Path basePath = HomeDir.get().toPath().resolve( "data" ).resolve( "dump" );

    @Activate
    public DumpServiceImpl( @Reference EventPublisher eventPublisher )
    {
        this.xpVersion = VersionInfo.get().getVersion();
        this.eventPublisher = eventPublisher;
    }

    @Override
    public DumpUpgradeResult upgrade( final SystemDumpUpgradeParams params )
    {
        if ( !SecurityHelper.isAdmin() )
        {
            throw new RepoDumpException( "Only admin role users can upgrade dumps" );
        }

        ensureBasePath();

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
        return Objects.requireNonNullElse( getDumpMeta( dumpName ).getModelVersion(), Version.emptyVersion );
    }

    private void updateDumpModelVersion( final String dumpName, final Version modelVersion )
    {
        final DumpMeta dumpMeta = getDumpMeta( dumpName );
        final DumpMeta updatedDumpMeta = DumpMeta.create( dumpMeta ).
            modelVersion( modelVersion ).
            build();

        final FileDumpWriter fileDumpWriter = FileDumpWriter.create( basePath, dumpName, blobStore );
        try (fileDumpWriter)
        {
            fileDumpWriter.writeDumpMetaData( updatedDumpMeta );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private DumpMeta getDumpMeta( final String dumpName )
    {
        final DumpReader dumpReader = FileDumpReader.create( null, basePath, dumpName );
        return dumpReader.getDumpMeta();
    }

    @Override
    public SystemDumpResult dump( final SystemDumpParams params )
    {
        if ( !SecurityHelper.isAdmin() )
        {
            throw new RepoDumpException( "Only admin role users can dump repositories" );
        }

        ensureBasePath();

        final DumpWriter writer = params.isArchive()
            ? ZipDumpWriter.create( basePath, params.getDumpName(), blobStore )
            : FileDumpWriter.create( basePath, params.getDumpName(), blobStore );
        try (writer)
        {
            final List<Repository> repositories = repositoryEntryService.findRepositoryEntryIds().stream().
                map( repositoryEntryService::getRepositoryEntry ).filter( Objects::nonNull ).collect( Collectors.toList() );

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
                    repository( repository ).
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

            LOG.info( "Dump completed" );
            return systemDumpResult;
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public SystemLoadResult load( final SystemLoadParams params )
    {
        if ( !SecurityHelper.isAdmin() )
        {
            throw new RepoLoadException( "Only admin role users can load repositories" );
        }

        ensureBasePath();

        final SystemLoadResult.Builder results = SystemLoadResult.create();

        final DumpReader dumpReader = params.isArchive()
            ? ZipDumpReader.create( params.getListener(), basePath, params.getDumpName() )
            : FileDumpReader.create( params.getListener(), basePath, params.getDumpName() );

        try (dumpReader)
        {
            verifyOrUpdateDumpVersion( params, dumpReader );

            final RepositoryIds dumpRepositories = dumpReader.getRepositories();

            if ( !dumpRepositories.contains( SystemConstants.SYSTEM_REPO_ID ) )
            {
                throw new RepoLoadException( "Cannot load system-dump; dump does not contain system repository" );
            }

            this.eventPublisher.publish( RepositoryEvents.restoreInitialized() );

            if ( params.getListener() != null )
            {
                final long branchesCount = dumpRepositories.
                    stream().
                    flatMap( repositoryId -> dumpReader.getBranches( repositoryId ).stream() ).
                    count();

                params.getListener().totalBranches( branchesCount );
            }

            final boolean includeVersions = params.isIncludeVersions();

            final RepositorySettings currentSystemSettings =
                repositoryEntryService.getRepositoryEntry( SystemConstants.SYSTEM_REPO_ID ).getSettings();

            final Map<RepositoryId, RepositorySettings> repoSettings = SYSTEM_REPO_IDS.stream()
                .collect(
                    Collectors.toMap( Function.identity(), repo -> repositoryEntryService.getRepositoryEntry( repo ).getSettings() ) );

            repositoryEntryService.findRepositoryEntryIds().
                stream().
                filter( Predicate.isEqual( SystemConstants.SYSTEM_REPO_ID ).
                    or( SYSTEM_REPO_IDS::contains ).
                    negate() ).
                forEach( this::doDeleteRepository );

            SYSTEM_REPO_IDS.forEach( this::doDeleteRepository );

            // system-repo must be deleted last
            doDeleteRepository( SystemConstants.SYSTEM_REPO_ID );

            // Load system repo to be able to read repository settings and data
            initAndLoad( includeVersions, results, dumpReader, SystemConstants.SYSTEM_REPO_ID, currentSystemSettings, null,
                         AttachedBinaries.empty() );

            SYSTEM_REPO_IDS.forEach( repositoryId -> {
                if ( dumpRepositories.contains( repositoryId ) )
                {
                    // Dump contains repository. Do a normal load.
                    initAndLoad( includeVersions, results, dumpReader, repositoryId, repoSettings.get( repositoryId ), new PropertyTree(),
                                 AttachedBinaries.empty() );
                }
                else
                {
                    // If it is an old dump it does not contain repo. It should be recreated with current settings
                    initializeRepo( repositoryId, repoSettings.get( repositoryId ), null, AttachedBinaries.empty() );
                    createRootNode( repositoryId );
                }

            } );

            // Load non-system repositories
            dumpRepositories.
                stream().
                filter( Predicate.isEqual( SystemConstants.SYSTEM_REPO_ID ).
                    or( SYSTEM_REPO_IDS::contains ).
                    negate() ).
                forEach( repositoryId -> {
                    final Repository repository = repositoryEntryService.getRepositoryEntry( repositoryId );
                    final RepositorySettings settings = repository.getSettings();
                    final PropertyTree data = repository.getData();
                    final AttachedBinaries attachedBinaries = repository.getAttachments();
                    initAndLoad( includeVersions, results, dumpReader, repositoryId, settings, data, attachedBinaries );
                } );

            this.eventPublisher.publish( RepositoryEvents.restored() );
            LOG.info( "Dump Load completed" );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
        return results.build();
    }

    void verifyOrUpdateDumpVersion( final SystemLoadParams params, final DumpReader dumpReader )
    {
        final Version modelVersion = Objects.requireNonNullElse( dumpReader.getDumpMeta().getModelVersion(), Version.emptyVersion );

        if ( modelVersion.getMajor() < DumpConstants.MODEL_VERSION.getMajor() )
        {
            if ( params.isUpgrade() )
            {
                if ( params.isArchive() )
                {
                    throw new RepoLoadException(
                        "Cannot load system-dump; upgrade is not possible on archived dump; unarchive and upgrade the system-dump" );
                }
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
    }

    void initAndLoad( final boolean includeVersions, final SystemLoadResult.Builder results, final DumpReader dumpReader,
                      final RepositoryId repository, RepositorySettings settings, PropertyTree data, AttachedBinaries attachedBinaries )
    {
        initializeRepo( repository, settings, data, attachedBinaries );
        doLoadRepository( repository, includeVersions, dumpReader, results );
    }

    private void doDeleteRepository( final RepositoryId repositoryId )
    {
        LOG.info( "Deleting repository [" + repositoryId + "]" );

        this.repositoryEntryService.deleteRepositoryEntry( repositoryId );
        this.nodeRepositoryService.delete( repositoryId );

        this.nodeStorageService.invalidate();
    }

    private void initializeRepo( final RepositoryId repositoryId, RepositorySettings settings, PropertyTree data,
                                 AttachedBinaries attachedBinaries )
    {
        final CreateRepositoryParams params = CreateRepositoryParams.create().
            repositoryId( repositoryId ).
            repositorySettings( settings ).
            data( data ).
            build();

        this.nodeRepositoryService.create( params );

        final Repository createRepositoryParams = Repository.create().
            id( repositoryId ).
            settings( settings ).
            data( data ).
            branches( Branches.from( RepositoryConstants.MASTER_BRANCH ) ).
            attachments( attachedBinaries ).
            build();

        this.repositoryEntryService.createRepositoryEntry( createRepositoryParams );
    }

    private void createRootNode( final RepositoryId repositoryId )
    {
        final Context rootNodeContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( repositoryId ).
            branch( RepositoryConstants.MASTER_BRANCH ).
            build();

        final InternalContext rootNodeInternalContext = InternalContext.create( rootNodeContext ).build();

        this.nodeStorageService.store( Node.createRoot().
            permissions( RepositoryConstants.DEFAULT_REPO_PERMISSIONS ).
            inheritPermissions( false ).
            childOrder( RepositoryConstants.DEFAULT_CHILD_ORDER ).
            build(), rootNodeInternalContext );

        rootNodeContext.runWith( () -> nodeService.refresh( RefreshMode.ALL ) );
    }

    private void doLoadRepository( final RepositoryId repositoryId, final boolean includeVersions, final DumpReader dumpReader,
                                   final SystemLoadResult.Builder builder )
    {
        LOG.info( "Loading repository [" + repositoryId + "]" );

        builder.add( RepoLoader.create().
            reader( dumpReader ).
            nodeService( this.nodeService ).
            blobStore( this.blobStore ).
            includeVersions( includeVersions ).
            repositoryEntryService( this.repositoryEntryService ).
            repositoryId( repositoryId ).
            build().
            execute() );
    }

    private void ensureBasePath()
    {
        try
        {
            Files.createDirectories( basePath );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot create dump directory", e );
        }
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
    public void setRepositoryEntryService( final RepositoryEntryService repositoryEntryService )
    {
        this.repositoryEntryService = repositoryEntryService;
    }

    @Reference
    public void setNodeRepositoryService( final NodeRepositoryService nodeRepositoryService )
    {
        this.nodeRepositoryService = nodeRepositoryService;
    }

    @Reference
    public void setNodeStorageService( final NodeStorageService nodeStorageService )
    {
        this.nodeStorageService = nodeStorageService;
    }

    public void setBasePath( final Path basePath )
    {
        this.basePath = basePath;
    }
}
