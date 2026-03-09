package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;
import com.google.common.io.LineProcessor;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.CommitsLoadResult;
import com.enonic.xp.dump.LoadError;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.dump.SystemLoadListener;
import com.enonic.xp.dump.VersionsLoadResult;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
import com.enonic.xp.repo.impl.dump.blobstore.BlobReference;
import com.enonic.xp.repo.impl.dump.blobstore.DumpBlobStoreUtils;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.serializer.json.DumpMetaJsonSerializer;
import com.enonic.xp.repo.impl.dump.serializer.json.JsonDumpSerializer;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.json.NodeVersionJsonSerializer;
import com.enonic.xp.repo.impl.repository.RepositoryEntry;
import com.enonic.xp.repo.impl.repository.RepositoryNodeTranslator;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.security.SystemConstants;

public class ZipDumpReaderV8
    implements DumpReader
{
    private static final Pattern ROOT_DUMP_DIR_PATTERN = Pattern.compile( "^([^/]+)/dump\\.json$" );

    private static final Set<String> RESERVED_DIRS = Set.of( "_versions", "_commits" );

    private final SystemLoadListener listener;

    private final PathRef basePath;

    private final ZipFile zipFile;

    private ZipDumpReaderV8( final SystemLoadListener listener, final PathRef basePath, final ZipFile zipFile )
    {
        this.listener = Objects.requireNonNullElse( listener, NoopSystemLoadListener.INSTANCE );
        this.basePath = basePath;
        this.zipFile = zipFile;
    }

    public static ZipDumpReaderV8 create( final SystemLoadListener listener, final Path basePath, final String dumpName )
    {
        Preconditions.checkArgument( FileNames.isSafeFileName( dumpName ) );
        try
        {
            final SeekableByteChannel seekableByteChannel =
                Files.newByteChannel( basePath.resolve( dumpName + ".zip" ), EnumSet.of( StandardOpenOption.READ ) );
            final ZipFile zipFile = ZipFile.builder().setSeekableByteChannel( seekableByteChannel ).get();

            return create( listener, dumpName, zipFile );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private static ZipDumpReaderV8 create( final SystemLoadListener listener, final String dumpName, final ZipFile zipFile )
    {
        if ( zipFile.getEntry( "dump.json" ) != null )
        {
            return new ZipDumpReaderV8( listener, PathRef.of(), zipFile );
        }
        else if ( zipFile.getEntry( dumpName + "/dump.json" ) != null )
        {
            return new ZipDumpReaderV8( listener, PathRef.of( dumpName ), zipFile );
        }
        else
        {
            final Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();

            while ( entries.hasMoreElements() )
            {
                final ZipArchiveEntry entry = entries.nextElement();

                final Matcher matcher = ROOT_DUMP_DIR_PATTERN.matcher( entry.getName() );

                if ( matcher.matches() )
                {
                    return new ZipDumpReaderV8( listener, PathRef.of( matcher.group( 1 ) ), zipFile );
                }
            }

            throw new RepoLoadException( "Archive is not a valid dump archive: [" + dumpName + "]" );
        }
    }

    @Override
    public RepositoryIds getRepositories()
    {
        final PathRef repoRootPath = basePath.resolve( "meta" );

        return listDirectories( repoRootPath ).map( RepositoryId::from ).collect( RepositoryIds.collector() );
    }

    @Override
    public Branches getBranches( final RepositoryId repositoryId )
    {
        final PathRef repoPath = basePath.resolve( "meta" ).resolve( repositoryId.toString() );

        return listDirectories( repoPath ).filter( name -> !RESERVED_DIRS.contains( name ) )
            .map( Branch::from )
            .collect( Branches.collector() );
    }

    @Override
    public BranchLoadResult loadBranch( final RepositoryId repositoryId, final Branch branch,
                                        final LineProcessor<EntryLoadResult> processor )
    {
        final PathRef dirPath = basePath.resolve( "meta" ).resolve( repositoryId.toString() ).resolve( branch.toString() );

        listener.loadingBranch( repositoryId, branch, getBranchSuccessfulCountFromMeta( repositoryId, branch ) );

        final BranchLoadResult.Builder builder = BranchLoadResult.create( branch );

        final EntriesLoadResult result = doLoadEntries( processor, dirPath );

        return builder.successful( result.getSuccessful() )
            .errors( result.getErrors().stream().map( error -> LoadError.error( error.getMessage() ) ).collect( Collectors.toList() ) )
            .build();
    }

    @Override
    public VersionsLoadResult loadVersions( final RepositoryId repositoryId, final LineProcessor<EntryLoadResult> processor )
    {
        final PathRef dirPath = basePath.resolve( "meta" ).resolve( repositoryId.toString() ).resolve( "_versions" );

        listener.loadingVersions( repositoryId );

        final VersionsLoadResult.Builder builder = VersionsLoadResult.create();

        final EntriesLoadResult result = doLoadEntries( processor, dirPath );

        return builder.successful( result.getSuccessful() )
            .errors( result.getErrors().stream().map( error -> LoadError.error( error.getMessage() ) ).collect( Collectors.toList() ) )
            .build();
    }

    @Override
    public CommitsLoadResult loadCommits( final RepositoryId repositoryId, final LineProcessor<EntryLoadResult> processor )
    {
        final PathRef dirPath = basePath.resolve( "meta" ).resolve( repositoryId.toString() ).resolve( "_commits" );

        listener.loadingCommits( repositoryId );

        final CommitsLoadResult.Builder builder = CommitsLoadResult.create();

        final EntriesLoadResult result = doLoadEntries( processor, dirPath );

        return builder.successful( result.getSuccessful() )
            .errors( result.getErrors().stream().map( error -> LoadError.error( error.getMessage() ) ).collect( Collectors.toList() ) )
            .build();
    }

    @Override
    public NodeStoreVersion get( final RepositoryId repositoryId, final NodeVersionKey nodeVersionKey )
    {
        final ByteSource dataBytes = getBlobByteSource( repositoryId, NodeConstants.NODE_SEGMENT_LEVEL, nodeVersionKey.getNodeBlobKey() );
        final ByteSource indexConfigBytes =
            getBlobByteSource( repositoryId, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL, nodeVersionKey.getIndexConfigBlobKey() );
        final ByteSource accessControlBytes =
            getBlobByteSource( repositoryId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL, nodeVersionKey.getAccessControlBlobKey() );

        try
        {
            return NodeVersionJsonSerializer.toNodeVersion( dataBytes, indexConfigBytes, accessControlBytes );
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot read node version", e );
        }
    }

    @Override
    public ByteSource getBinary( final RepositoryId repositoryId, final BlobKey blobKey )
    {
        return getBlobByteSource( repositoryId, NodeConstants.BINARY_SEGMENT_LEVEL, blobKey );
    }

    @Override
    public DumpMeta getDumpMeta()
    {
        final PathRef dumpMetaFile = basePath.resolve( "dump.json" );
        try (InputStream stream = openZipEntryStream( dumpMetaFile ))
        {
            return new DumpMetaJsonSerializer().toDumpMeta( new String( stream.readAllBytes(), StandardCharsets.UTF_8 ) );
        }
        catch ( IOException e )
        {
            throw new RepoLoadException( "Cannot read dump-meta file", e );
        }
    }

    @Override
    public List<RepositoryEntry> getRepositoryEntries( final RepositoryIds repositoryIds )
    {
        final PathRef dirPath = basePath.resolve( "meta" )
            .resolve( SystemConstants.SYSTEM_REPO_ID.toString() )
            .resolve( SystemConstants.BRANCH_SYSTEM.toString() );

        final String prefix = dirPath.asString() + "/";

        final JsonDumpSerializer serializer = new JsonDumpSerializer();
        final NodeIds targetNodeIds =
            repositoryIds.stream().map( repositoryId -> NodeId.from( repositoryId.toString() ) ).collect( NodeIds.collector() );

        final List<RepositoryEntry> result = new ArrayList<>();

        listZipEntries( prefix ).forEach( zipEntry -> {
            if ( result.size() >= repositoryIds.getSize() )
            {
                return;
            }

            try (InputStream stream = zipFile.getInputStream( zipEntry ))
            {
                final String content = new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
                final BranchDumpEntry branchDumpEntry = serializer.toBranchMetaEntry( content );

                if ( targetNodeIds.contains( branchDumpEntry.nodeId() ) )
                {
                    final NodeVersionKey nodeVersionKey = branchDumpEntry.meta().nodeVersionKey();
                    final NodeStoreVersion nodeStoreVersion = get( SystemConstants.SYSTEM_REPO_ID, nodeVersionKey );

                    final Node node = Node.create()
                        .id( branchDumpEntry.nodeId() )
                        .childOrder( ChildOrder.defaultOrder() )
                        .data( nodeStoreVersion.data() )
                        .name( branchDumpEntry.nodeId().toString() )
                        .parentPath( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH )
                        .permissions( nodeStoreVersion.permissions() )
                        .attachedBinaries( nodeStoreVersion.attachedBinaries() )
                        .build();

                    result.add( RepositoryNodeTranslator.toRepository( node ) );
                }
            }
            catch ( IOException e )
            {
                throw new RepoDumpException( "Cannot read repository entry from dump", e );
            }
        } );

        return result;
    }

    @Override
    public void close()
        throws IOException
    {
        zipFile.close();
    }

    private EntriesLoadResult doLoadEntries( final LineProcessor<EntryLoadResult> processor, final PathRef dirPath )
    {
        final EntriesLoadResult.Builder result = EntriesLoadResult.create();

        final String prefix = dirPath.asString() + "/";

        listZipEntries( prefix ).forEach( zipEntry -> {
            try (InputStream stream = zipFile.getInputStream( zipEntry ))
            {
                final String content = new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
                processor.processLine( content );
                listener.entryLoaded();
                result.add( processor.getResult() );
            }
            catch ( IOException e )
            {
                throw new RepoDumpException( "Cannot read meta-data", e );
            }
        } );

        return result.build();
    }

    private Stream<ZipArchiveEntry> listZipEntries( final String prefix )
    {
        return StreamSupport.stream( Spliterators.spliteratorUnknownSize( zipFile.getEntries().asIterator(), Spliterator.ORDERED ), false )
            .filter( entry -> entry.getName().startsWith( prefix ) )
            .filter( entry -> entry.getName().indexOf( '/', prefix.length() ) == -1 )
            .filter( entry -> !entry.isDirectory() );
    }

    private Stream<String> listDirectories( final PathRef parentPath )
    {
        final String prefix = parentPath.asString() + "/";
        return StreamSupport.stream( Spliterators.spliteratorUnknownSize( zipFile.getEntries().asIterator(), Spliterator.ORDERED ), false )
            .map( ZipArchiveEntry::getName )
            .filter( name -> name.startsWith( prefix ) )
            .filter( name -> name.indexOf( '/', prefix.length() ) != -1 )
            .map( name -> name.substring( prefix.length(), name.indexOf( '/', prefix.length() ) ) )
            .distinct();
    }

    private ByteSource getBlobByteSource( final RepositoryId repositoryId, final SegmentLevel segmentLevel, final BlobKey blobKey )
    {
        final BlobReference reference = new BlobReference( RepositorySegmentUtils.toSegment( repositoryId, segmentLevel ), blobKey );
        final String zipEntryName = DumpBlobStoreUtils.getBlobPathRef( basePath, reference ).asString();
        return new ZipEntryByteSource( zipFile, zipEntryName );
    }

    private InputStream openZipEntryStream( final PathRef path )
        throws IOException
    {
        final ZipArchiveEntry entry = zipFile.getEntry( path.asString() );
        if ( entry == null )
        {
            throw new IOException( "Zip entry not found: " + path.asString() );
        }
        return zipFile.getInputStream( entry );
    }

    private Long getBranchSuccessfulCountFromMeta( final RepositoryId repositoryId, final Branch branch )
    {
        final SystemDumpResult systemDumpResult = getDumpMeta().getSystemDumpResult();
        if ( systemDumpResult != null )
        {
            final RepoDumpResult repoDumpResult = systemDumpResult.get( repositoryId );
            if ( repoDumpResult != null )
            {
                final BranchDumpResult branchDumpResult = repoDumpResult.get( branch );
                if ( branchDumpResult != null )
                {
                    return branchDumpResult.getSuccessful();
                }
            }
        }
        return null;
    }

    private static class ZipEntryByteSource
        extends ByteSource
    {
        final String zipEntryName;

        final ZipFile zipFile;

        ZipEntryByteSource( final ZipFile zipFile, final String zipEntryName )
        {
            this.zipFile = zipFile;
            this.zipEntryName = zipEntryName;
        }

        @Override
        public InputStream openStream()
            throws IOException
        {
            return zipFile.getInputStream( zipFile.getEntry( zipEntryName ) );
        }
    }

    private enum NoopSystemLoadListener
        implements SystemLoadListener
    {
        INSTANCE;

        @Override
        public void totalBranches( final long total )
        {
        }

        @Override
        public void loadingBranch( final RepositoryId repositoryId, final Branch branch, final Long total )
        {
        }

        @Override
        public void loadingVersions( final RepositoryId repositoryId )
        {
        }

        @Override
        public void loadingCommits( final RepositoryId repositoryId )
        {
        }

        @Override
        public void entryLoaded()
        {
        }
    }
}
