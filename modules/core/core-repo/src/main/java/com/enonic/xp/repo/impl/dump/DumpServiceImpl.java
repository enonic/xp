package com.enonic.xp.repo.impl.dump;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import com.enonic.xp.core.internal.Millis;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.DumpUpgradeResult;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpParams;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.dump.SystemDumpUpgradeParams;
import com.enonic.xp.dump.SystemLoadParams;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repo.impl.RepositoryEvents;
import com.enonic.xp.repo.impl.SecurityHelper;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.config.RepoConfigurationDynamic;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.reader.DumpReader;
import com.enonic.xp.repo.impl.dump.reader.NodeLoader;
import com.enonic.xp.repo.impl.dump.reader.ZipDumpReaderV8;
import com.enonic.xp.repo.impl.dump.upgrade.DumpUpgraderRunner;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repo.impl.dump.writer.ZipDumpWriterV8;
import com.enonic.xp.repo.impl.repository.NodeRepositoryService;
import com.enonic.xp.repo.impl.repository.RepositoryCreator;
import com.enonic.xp.repo.impl.repository.RepositoryEntry;
import com.enonic.xp.repo.impl.repository.RepositoryEntryService;
import com.enonic.xp.repo.impl.repository.RepositorySettings;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.server.VersionInfo;

import static com.google.common.base.Strings.nullToEmpty;

@Component(immediate = true)
@SuppressWarnings("WeakerAccess")
public class DumpServiceImpl
    implements DumpService
{
    private static final Logger LOG = LoggerFactory.getLogger( DumpServiceImpl.class );


    public static final Predicate<RepositoryId> ANY_SYSTEM_REPO_PREDICATE =
        Predicate.<RepositoryId>isEqual( SystemConstants.SYSTEM_REPO_ID ).or( SystemRepoDefaults.SYSTEM_REPO_DEFAULTS::containsKey );

    private final BlobStore blobStore;

    private final NodeService nodeService;

    private final RepositoryEntryService repositoryEntryService;

    private final NodeRepositoryService nodeRepositoryService;

    private final NodeStorageService nodeStorageService;

    private final BranchService branchService;

    private final EventPublisher eventPublisher;

    private final String xpVersion;

    private final RepoConfigurationDynamic repoConfiguration;

    @Activate
    public DumpServiceImpl( @Reference EventPublisher eventPublisher, @Reference BlobStore blobStore, @Reference NodeService nodeService,
                            @Reference RepositoryEntryService repositoryEntryService,
                            @Reference NodeRepositoryService nodeRepositoryService, @Reference NodeStorageService nodeStorageService,
                            @Reference BranchService branchService, @Reference RepoConfigurationDynamic repoConfiguration )
    {
        this.xpVersion = VersionInfo.get().getVersion();
        this.blobStore = blobStore;
        this.nodeService = nodeService;
        this.repositoryEntryService = repositoryEntryService;
        this.nodeRepositoryService = nodeRepositoryService;
        this.nodeStorageService = nodeStorageService;
        this.branchService = branchService;
        this.eventPublisher = eventPublisher;
        this.repoConfiguration = repoConfiguration;
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

        final Path basePath = ensureBasePath();

        return new DumpUpgraderRunner().upgrade( basePath, dumpName, params.getUpgradeListener() );
    }

    @Override
    public SystemDumpResult dump( final SystemDumpParams params )
    {
        if ( !SecurityHelper.isAdmin() )
        {
            throw new RepoDumpException( "Only admin role users can dump repositories" );
        }

        final Path basePath = ensureBasePath();

        final DumpWriter writer = ZipDumpWriterV8.create( basePath, params.getDumpName(), blobStore );
        try (writer)
        {
            final SystemDumpResult systemDumpResult =
                !params.getRepositories().isEmpty() ? doPartialDump( params, writer ) : doFullDump( params, writer );

            writer.writeDumpMetaData( DumpMeta.create()
                                          .xpVersion( this.xpVersion )
                                          .modelVersion( DumpConstants.MODEL_VERSION )
                                          .timestamp( Millis.now() )
                                          .systemDumpResult( systemDumpResult )
                                          .build() );

            LOG.info( "Dump completed" );
            return systemDumpResult;
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private SystemDumpResult doFullDump( final SystemDumpParams params, final DumpWriter writer )
    {
        final Map<RepositoryId, Branches> repositoryBranches = repositoryEntryService.findRepositoryEntryIds()
            .stream()
            .map( repositoryEntryService::getRepositoryEntry )
            .filter( Objects::nonNull )
            .filter( entry -> !entry.isTransient() )
            .map( RepositoryEntry::getId )
            .collect( Collectors.toMap( Function.identity(), this::resolveBranches ) );

        if ( params.getListener() != null )
        {
            final long branchesCount = repositoryBranches.values().stream().mapToLong( Branches::getSize ).sum();

            params.getListener().totalBranches( branchesCount );
        }

        final SystemDumpResult.Builder dumpResults = SystemDumpResult.create();

        return dumpRepositories( params, writer, repositoryBranches, dumpResults );
    }

    private SystemDumpResult doPartialDump( final SystemDumpParams params, final DumpWriter writer )
    {
        final List<RepositoryId> systemRepos = params.getRepositories().stream().filter( ANY_SYSTEM_REPO_PREDICATE ).toList();

        if ( !systemRepos.isEmpty() )
        {
            throw new RepoDumpException( "System repositories " + systemRepos + " cannot be dumped partially" );
        }

        final RepositoryIds allRepositoryIds = repositoryEntryService.findRepositoryEntryIds();

        final List<RepositoryId> missingRepositories =
            params.getRepositories().stream().filter( id -> !allRepositoryIds.contains( id ) ).toList();

        if ( !missingRepositories.isEmpty() )
        {
            LOG.warn( "Requested repositories not found and will be skipped during dump: {}", missingRepositories );
        }

        final List<RepositoryId> reposToDump = params.getRepositories().stream().filter( allRepositoryIds::contains ).toList();

        final Map<RepositoryId, Branches> repoBranches =
            reposToDump.stream().collect( Collectors.toMap( Function.identity(), this::resolveBranches ) );

        if ( params.getListener() != null )
        {
            params.getListener().totalBranches( repoBranches.values().stream().mapToLong( Branches::getSize ).sum() );
        }

        final SystemDumpResult.Builder dumpResults = SystemDumpResult.create();

        // Dump system-repo with only the RepositoryEntry nodes for selected repositories
        final NodeIds systemRepoNodeIds = reposToDump.stream().map( NodeId::from ).collect( NodeIds.collector() );

        RepoDumper.create()
            .writer( writer )
            .includeVersions( false )
            .includeBinaries( params.isIncludeBinaries() )
            .nodeService( this.nodeService )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branches( Branches.from( SystemConstants.BRANCH_SYSTEM ) )
            .nodeIds( systemRepoNodeIds )
            .build()
            .execute();

        // Dump each selected repository fully
        return dumpRepositories( params, writer, repoBranches, dumpResults );
    }

    private SystemDumpResult dumpRepositories( final SystemDumpParams params, final DumpWriter writer,
                                               final Map<RepositoryId, Branches> repositoryBranches,
                                               final SystemDumpResult.Builder dumpResults )
    {
        for ( final var entry : repositoryBranches.entrySet() )
        {
            final RepoDumpResult result = RepoDumper.create()
                .writer( writer )
                .includeVersions( params.isIncludeVersions() )
                .includeBinaries( params.isIncludeBinaries() )
                .nodeService( this.nodeService )
                .repositoryId( entry.getKey() )
                .branches( entry.getValue() )
                .maxVersions( params.getMaxVersions() )
                .maxAge( params.getMaxAge() )
                .listener( params.getListener() )
                .build()
                .execute();

            dumpResults.add( result );
        }

        return dumpResults.build();
    }

    @Override
    public SystemLoadResult load( final SystemLoadParams params )
    {
        if ( !SecurityHelper.isAdmin() )
        {
            throw new RepoLoadException( "Only admin role users can load repositories" );
        }

        final Path basePath = ensureBasePath();

        final SystemLoadResult.Builder results = SystemLoadResult.create();

        final String dumpName = params.isUpgrade() ? verifyOrUpgradeDump( basePath, params ) : params.getDumpName();

        try (DumpReader dumpReader = ZipDumpReaderV8.create( params.getListener(), basePath, dumpName ))
        {
            final RepositoryIds dumpRepositories = dumpReader.getRepositories();

            if ( !dumpRepositories.contains( SystemConstants.SYSTEM_REPO_ID ) )
            {
                throw new RepoLoadException( "Cannot load system-dump; dump does not contain system repository" );
            }

            if ( !params.getRepositories().isEmpty() )
            {
                doPartialLoad( params, dumpReader, dumpRepositories, results );
            }
            else
            {
                final boolean isPartialDump =
                    dumpReader.getRepositoryEntries( RepositoryIds.from( SystemConstants.SYSTEM_REPO_ID ) ).isEmpty();
                if ( isPartialDump )
                {
                    throw new RepoLoadException(
                        "Cannot load partial dump as a full dump; specify repositories to load or use a full dump" );
                }
                doFullLoad( params, dumpReader, dumpRepositories, results );
            }
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
        return results.build();
    }

    private void doFullLoad( final SystemLoadParams params, final DumpReader dumpReader, final RepositoryIds dumpRepositories,
                             final SystemLoadResult.Builder results )
    {
        this.eventPublisher.publish( RepositoryEvents.restoreInitialized() );

        if ( params.getListener() != null )
        {
            final long branchesCount =
                dumpRepositories.stream().flatMap( repositoryId -> dumpReader.getBranches( repositoryId ).stream() ).count();

            params.getListener().totalBranches( branchesCount );
        }

        final boolean includeVersions = params.isIncludeVersions();

        final RepositorySettings currentSystemSettings =
            repositoryEntryService.getRepositoryEntry( SystemConstants.SYSTEM_REPO_ID ).getSettings();

        final Map<RepositoryId, RepositorySettings> repoSettings = SystemRepoDefaults.SYSTEM_REPO_DEFAULTS.keySet()
            .stream()
            .collect( Collectors.toMap( Function.identity(), repo -> repositoryEntryService.getRepositoryEntry( repo ).getSettings() ) );

        repositoryEntryService.findRepositoryEntryIds()
            .stream()
            .filter( ANY_SYSTEM_REPO_PREDICATE.negate() )
            .forEach( this::doDeleteRepository );

        SystemRepoDefaults.SYSTEM_REPO_DEFAULTS.keySet().forEach( this::doDeleteRepository );

        // system-repo must be deleted last
        doDeleteRepository( SystemConstants.SYSTEM_REPO_ID );

        final RepositoryCreator repositoryCreator =
            new RepositoryCreator( nodeRepositoryService, nodeStorageService, repositoryEntryService );

        // Load system-repo to be able to read repository settings and data
        repositoryCreator.createSystemRepository( currentSystemSettings );

        doLoadRepository( SystemConstants.SYSTEM_REPO_ID, includeVersions, dumpReader, results );

        // Transient repositories are not part of the dump. Clean them up.
        final RepositoryIds repositoryEntryIds = repositoryEntryService.findRepositoryEntryIds();
        for ( RepositoryId repositoryId : repositoryEntryIds )
        {
            if ( repositoryEntryService.getRepositoryEntry( repositoryId ).isTransient() )
            {
                repositoryEntryService.deleteRepositoryEntry( repositoryId );
            }
        }

        // Load other system repositories
        SystemRepoDefaults.SYSTEM_REPO_DEFAULTS.keySet().forEach( repositoryId -> {
            repositoryCreator.createRepository( CreateRepositoryParams.create()
                                                    .repositoryId( repositoryId )
                                                    .rootPermissions( SystemRepoDefaults.SYSTEM_REPO_DEFAULTS.get( repositoryId ).acl() )
                                                    .rootChildOrder(
                                                        SystemRepoDefaults.SYSTEM_REPO_DEFAULTS.get( repositoryId ).childOrder() )
                                                    .build(), repoSettings.get( repositoryId ), AttachedBinaries.empty(), true );
            if ( dumpRepositories.contains( repositoryId ) )
            {
                // Dump contains system.X repository. Do a normal load.
                doLoadRepository( repositoryId, includeVersions, dumpReader, results );
            }
        } );

        // Load non-system repositories
        dumpRepositories.stream().filter( ANY_SYSTEM_REPO_PREDICATE.negate() ).forEach( repositoryId -> {
            final RepositoryEntry repository = repositoryEntryService.getRepositoryEntry( repositoryId );

            repositoryCreator.createRepository( CreateRepositoryParams.create()
                                                    .repositoryId( repositoryId )
                                                    .rootPermissions( RepositoryConstants.DEFAULT_REPO_PERMISSIONS )
                                                    .rootChildOrder( RepositoryConstants.DEFAULT_CHILD_ORDER )
                                                    .data( repository.getData() )
                                                    .build(), repository.getSettings(), repository.getAttachments(), true );

            doLoadRepository( repositoryId, includeVersions, dumpReader, results );
        } );

        this.eventPublisher.publish( RepositoryEvents.restored() );
        LOG.info( "Dump Load completed" );
    }

    private void doPartialLoad( final SystemLoadParams params, final DumpReader dumpReader, final RepositoryIds dumpRepositories,
                                final SystemLoadResult.Builder results )
    {
        final List<RepositoryId> systemRepos = params.getRepositories().stream().filter( ANY_SYSTEM_REPO_PREDICATE ).toList();

        if ( !systemRepos.isEmpty() )
        {
            throw new RepoLoadException( "System repositories " + systemRepos + " can be loaded only in full dump" );
        }

        final List<RepositoryId> reposToLoad = params.getRepositories().stream().filter( dumpRepositories::contains ).toList();

        this.eventPublisher.publish( RepositoryEvents.restoreInitialized() );

        if ( params.getListener() != null )
        {
            final long branchesCount =
                reposToLoad.stream().flatMap( repositoryId -> dumpReader.getBranches( repositoryId ).stream() ).count();

            params.getListener().totalBranches( branchesCount );
        }

        final boolean includeVersions = params.isIncludeVersions();

        final Map<RepositoryId, RepositoryEntry> dumpEntries = dumpReader.getRepositoryEntries( RepositoryIds.from( reposToLoad ) )
            .stream()
            .collect( Collectors.toMap( RepositoryEntry::getId, Function.identity() ) );

        final RepositoryCreator repositoryCreator =
            new RepositoryCreator( nodeRepositoryService, nodeStorageService, repositoryEntryService );

        for ( final RepositoryId repositoryId : reposToLoad )
        {
            final RepositoryEntry repository = dumpEntries.get( repositoryId );

            if ( repository != null )
            {
                doDeleteRepository( repositoryId );

                repositoryCreator.createRepository( CreateRepositoryParams.create()
                                                        .repositoryId( repositoryId )
                                                        .rootPermissions( RepositoryConstants.DEFAULT_REPO_PERMISSIONS )
                                                        .rootChildOrder( RepositoryConstants.DEFAULT_CHILD_ORDER )
                                                        .data( repository.getData() )
                                                        .build(), repository.getSettings(), repository.getAttachments(), true );

                doLoadRepository( repositoryId, includeVersions, dumpReader, results );
            }
            else
            {
                LOG.warn( "Repository entry not found in dump for repository [{}]", repositoryId );
            }
        }

        this.eventPublisher.publish( RepositoryEvents.restored() );
        LOG.info( "Partial Dump Load completed" );
    }

    String verifyOrUpgradeDump( final Path basePath, final SystemLoadParams params )
    {
        return new DumpUpgraderRunner().upgrade( basePath, params.getDumpName(), null ).getDumpName();
    }

    private void doDeleteRepository( final RepositoryId repositoryId )
    {
        LOG.info( "Deleting repository [{}]", repositoryId );

        this.repositoryEntryService.deleteRepositoryEntry( repositoryId );
        this.nodeRepositoryService.delete( repositoryId );

        this.nodeStorageService.invalidate();
    }

    private void doLoadRepository( final RepositoryId repositoryId, final boolean includeVersions, final DumpReader dumpReader,
                                   final SystemLoadResult.Builder builder )
    {
        LOG.info( "Loading repository [{}]", repositoryId );

        builder.add( RepoLoader.create()
                         .reader( dumpReader )
                         .nodeLoader( new NodeLoader( this.nodeStorageService ) )
                         .blobStore( this.blobStore )
                         .includeVersions( includeVersions )
                         .repositoryId( repositoryId )
                         .build()
                         .execute() );
        nodeRepositoryService.refresh( repositoryId );
    }

    private Branches resolveBranches( final RepositoryId repositoryId )
    {
        return this.branchService.getBranches( NodeId.ROOT, repositoryId );
    }

    private Path ensureBasePath()
    {
        try
        {
            return Files.createDirectories( repoConfiguration.getDumpsDir() );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot create dump directory", e );
        }
    }
}
