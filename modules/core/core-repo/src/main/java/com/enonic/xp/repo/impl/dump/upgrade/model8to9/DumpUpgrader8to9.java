package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.DumpUpgradeStepResult;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.reader.DumpReaderV7;
import com.enonic.xp.repo.impl.dump.serializer.json.BranchDumpEntryJson;
import com.enonic.xp.repo.impl.dump.serializer.json.CommitDumpEntryJson;
import com.enonic.xp.repo.impl.dump.serializer.json.JsonDumpSerializer;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionsDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.BranchEntryUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.DumpUpgradeException;
import com.enonic.xp.repo.impl.dump.upgrade.DumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionEntryUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.NodeVersionUpgrader;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.json.NodeVersionJsonSerializer;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.util.Version;

public class DumpUpgrader8to9
    implements DumpUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( DumpUpgrader8to9.class );

    public static final Version MODEL_VERSION = new Version( 9, 0, 0 );

    private final DumpReaderV7 dumpReader;

    private DumpWriter dumpWriter;

    private DumpUpgradeStepResult.Builder result;

    private final Map<String, String> blobKeyMapping = new HashMap<>();

    public DumpUpgrader8to9( final DumpReaderV7 dumpReader )
    {
        this.dumpReader = dumpReader;
    }

    @Override
    public DumpUpgradeStepResult upgrade( final DumpWriter dumpWriter )
    {
        this.dumpWriter = dumpWriter;

        final DumpMeta sourceDumpMeta = this.dumpReader.getDumpMeta();

        this.result = DumpUpgradeStepResult.create()
            .initialVersion( sourceDumpMeta.getModelVersion() )
            .upgradedVersion( getModelVersion() )
            .stepName( getName() );

        this.dumpWriter.writeDumpMetaData(
            DumpMeta.create( sourceDumpMeta ).xpVersion( VersionInfo.get().getVersion() ).modelVersion( getModelVersion() ).build() );
        this.dumpReader.getRepositories().forEach( this::upgradeRepository );
        return result.build();
    }

    protected void upgradeRepository( final RepositoryId repositoryId )
    {
        blobKeyMapping.clear();

        dumpReader.getVersions( repositoryId ).ifPresent( ref -> upgradeVersionEntries( repositoryId, ref ) );
        dumpReader.getCommits( repositoryId ).ifPresent( ref -> upgradeCommitEntries( repositoryId, ref ) );

        final List<AdditionalDumpEntry> additionalEntries = createAdditionalNodes( repositoryId );

        for ( Branch branch : dumpReader.getBranches( repositoryId ) )
        {
            final List<BranchDumpEntryJson> additionalBranchEntries =
                additionalEntries.stream().filter( e -> e.branches().contains( branch ) ).map( AdditionalDumpEntry::branchEntry ).toList();

            final PathRef entriesFile = dumpReader.getBranchEntries( repositoryId, branch )
                .orElseThrow( () -> new DumpUpgradeException(
                    "Branch entries file missing for repository [" + repositoryId + "] and branch [" + branch + "]" ) );
            upgradeBranchEntries( repositoryId, branch, entriesFile, additionalBranchEntries );
        }
    }

    protected void upgradeVersionEntries( final RepositoryId repositoryId, final PathRef entriesFile )
    {
        dumpWriter.openVersionsMeta( repositoryId );
        try
        {
            processEntries( ( entryContent, entryName ) -> {
                result.processed();
                try
                {
                    final byte[] upgradedEntryContent = processVersionEntry( repositoryId, entryContent );
                    dumpWriter.writeRawEntry( entryName, upgradedEntryContent );
                }
                catch ( Exception e )
                {
                    result.error();
                    LOG.error( "Error while upgrading version entry [{}]", entryName, e );
                }
            }, entriesFile );
        }
        finally
        {
            dumpWriter.closeMeta();
        }
    }

    private void upgradeCommitEntries( final RepositoryId repositoryId, final PathRef entriesFile )
    {
        dumpWriter.openCommitsMeta( repositoryId );
        try
        {
            processEntries( ( entryContent, entryName ) -> {
                final CommitDumpEntryJson commitEntry = JsonDumpSerializer.readValue( entryContent, CommitDumpEntryJson.class );
                final CommitDumpEntryJson upgraded = CommitDumpEntryJson.from( CommitDumpEntryJson.fromJson( commitEntry ) );
                dumpWriter.writeRawEntry( entryName, JsonDumpSerializer.serialize( upgraded ) );
            }, entriesFile );
        }
        finally
        {
            dumpWriter.closeMeta();
        }
    }

    private byte[] processVersionEntry( final RepositoryId repositoryId, final byte[] entryContent )
    {
        final VersionsDumpEntryJson versionsDumpEntryJson = JsonDumpSerializer.readValue( entryContent, VersionsDumpEntryJson.class );

        final VersionsDumpEntryJson.Builder resultBuilder = VersionsDumpEntryJson.create().nodeId( versionsDumpEntryJson.getNodeId() );

        for ( VersionDumpEntryJson versionDumpEntryJson : versionsDumpEntryJson.getVersions() )
        {
            resultBuilder.version( processVersionMeta( versionDumpEntryJson, repositoryId ) );
        }

        return JsonDumpSerializer.serialize( resultBuilder.build() );
    }

    private VersionDumpEntryJson processVersionMeta( final VersionDumpEntryJson versionDumpEntryJson, final RepositoryId repositoryId )
    {
        final Segment nodeSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.NODE_SEGMENT_LEVEL );
        final Segment indexConfigSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL );
        final Segment accessControlSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL );

        final NodeStoreVersion dumpEntry = readNodeVersion( versionDumpEntryJson, nodeSegment, indexConfigSegment, accessControlSegment );

        final NodeStoreVersion upgraded = upgradeNodeVersion( repositoryId, dumpEntry );

        final NodeStoreVersion withUpgradedBinaries = copyBinaryBlobs( upgraded != null ? upgraded : dumpEntry, repositoryId );

        final NodeStoreVersion toWrite = withUpgradedBinaries != null ? withUpgradedBinaries : ( upgraded != null ? upgraded : dumpEntry );

        final BlobKey newNodeBlobKey = writeNodeStoreVersionBlob( toWrite, nodeSegment, NodeVersionJsonSerializer::toNodeVersionBytes );
        final BlobKey newIndexConfigBlobKey =
            writeNodeStoreVersionBlob( toWrite, indexConfigSegment, NodeVersionJsonSerializer::toIndexConfigDocumentBytes );
        final BlobKey newAccessControlBlobKey =
            writeNodeStoreVersionBlob( toWrite, accessControlSegment, NodeVersionJsonSerializer::toAccessControlBytes );

        blobKeyMapping.put( versionDumpEntryJson.getNodeBlobKey(), newNodeBlobKey.toString() );
        blobKeyMapping.put( versionDumpEntryJson.getIndexConfigBlobKey(), newIndexConfigBlobKey.toString() );
        blobKeyMapping.put( versionDumpEntryJson.getAccessControlBlobKey(), newAccessControlBlobKey.toString() );

        VersionDumpEntryJson result = VersionDumpEntryJson.create( versionDumpEntryJson )
            .nodeBlobKey( newNodeBlobKey.toString() )
            .indexConfigBlobKey( newIndexConfigBlobKey.toString() )
            .accessControlBlobKey( newAccessControlBlobKey.toString() )
            .build();

        for ( NodeVersionEntryUpgrader upgrader : List.of( new NodePathTrimUpgrader() ) )
        {
            result = upgrader.upgradeVersionEntry( result );
        }

        return result;
    }

    protected void upgradeBranchEntries( final RepositoryId repositoryId, final Branch branch, final PathRef entriesFile,
                                         final List<BranchDumpEntryJson> additionalBranchEntries )
    {
        dumpWriter.openBranchMeta( repositoryId, branch );
        try
        {
            processEntries( ( entryContent, entryName ) -> {
                result.processed();
                try
                {
                    final BranchDumpEntryJson branchEntry = JsonDumpSerializer.readValue( entryContent, BranchDumpEntryJson.class );

                    final BranchDumpEntryJson updatedBranchEntry = upgradeBranchEntry( branchEntry );
                    dumpWriter.writeRawEntry( entryName, JsonDumpSerializer.serialize( updatedBranchEntry ) );
                }
                catch ( Exception e )
                {
                    result.error();
                    LOG.error( "Error while upgrading branch entry [{}]", entryName, e );
                }
            }, entriesFile );

            for ( BranchDumpEntryJson additionalEntry : additionalBranchEntries )
            {
                dumpWriter.writeRawEntry( additionalEntry.getNodeId() + ".json", JsonDumpSerializer.serialize( additionalEntry ) );
            }
        }
        finally
        {
            dumpWriter.closeMeta();
        }
    }

    private BranchDumpEntryJson upgradeBranchEntry( final BranchDumpEntryJson branchEntry )
    {
        final VersionDumpEntryJson meta = branchEntry.getMeta();

        final VersionDumpEntryJson.Builder metaBuilder = VersionDumpEntryJson.create( meta );

        final String newNodeBlobKey = blobKeyMapping.get( meta.getNodeBlobKey() );
        if ( newNodeBlobKey != null )
        {
            metaBuilder.nodeBlobKey( newNodeBlobKey );
        }

        final String newIndexConfigBlobKey = blobKeyMapping.get( meta.getIndexConfigBlobKey() );
        if ( newIndexConfigBlobKey != null )
        {
            metaBuilder.indexConfigBlobKey( newIndexConfigBlobKey );
        }

        final String newAccessControlBlobKey = blobKeyMapping.get( meta.getAccessControlBlobKey() );
        if ( newAccessControlBlobKey != null )
        {
            metaBuilder.accessControlBlobKey( newAccessControlBlobKey );
        }
        final BranchDumpEntryJson.Builder builder = BranchDumpEntryJson.create( branchEntry ).meta( metaBuilder.build() );

        final List<String> remappedBinaries = remapBinaries( branchEntry.getBinaries() );
        builder.binaries( remappedBinaries );

        BranchDumpEntryJson result = builder.build();

        for ( BranchEntryUpgrader upgrader : List.of( new NodePathTrimUpgrader() ) )
        {
            result = upgrader.upgradeBranchEntry( result );
        }

        return result;
    }

    private @Nullable List<String> remapBinaries( final List<String> binaries )
    {
        if ( binaries == null || binaries.isEmpty() )
        {
            return binaries;
        }

        final List<String> remapped = new ArrayList<>( binaries.size() );
        for ( String binaryKey : binaries )
        {
            final String newKey = blobKeyMapping.get( binaryKey );
            if ( newKey != null )
            {
                remapped.add( newKey );
            }
            else
            {
                LOG.warn( "Cannot remap binary key [{}] for branch entry. Skipping", binaryKey );
            }
        }
        return remapped;
    }

    private @Nullable NodeStoreVersion copyBinaryBlobs( final NodeStoreVersion dumpEntry, final RepositoryId repositoryId )
    {
        final Segment binarySegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.BINARY_SEGMENT_LEVEL );

        boolean keysChanged = false;
        final AttachedBinaries.Builder updatedBinaries = AttachedBinaries.create();

        for ( AttachedBinary binary : dumpEntry.attachedBinaries() )
        {
            final BlobRecord binaryRecord = dumpReader.getRecord( binarySegment, BlobKey.from( binary.getBlobKey() ) );
            final BlobKey newBlobKey = dumpWriter.addBlobRecord( binarySegment, binaryRecord.getBytes() );

            if ( !newBlobKey.toString().equals( binary.getBlobKey() ) )
            {
                keysChanged = true;
                blobKeyMapping.put( binary.getBlobKey(), newBlobKey.toString() );
            }
            updatedBinaries.add( new AttachedBinary( binary.getBinaryReference(), newBlobKey.toString() ) );
        }

        if ( keysChanged )
        {
            return NodeStoreVersion.create( dumpEntry ).attachedBinaries( updatedBinaries.build() ).build();
        }
        return null;
    }

    private NodeStoreVersion readNodeVersion( final VersionDumpEntryJson versionDumpEntryJson, final Segment nodeSegment,
                                              final Segment indexConfigSegment, final Segment accessControlSegment )
    {
        final BlobRecord nodeRecord = dumpReader.getRecord( nodeSegment, BlobKey.from( versionDumpEntryJson.getNodeBlobKey() ) );
        final BlobRecord indexConfigRecord =
            dumpReader.getRecord( indexConfigSegment, BlobKey.from( versionDumpEntryJson.getIndexConfigBlobKey() ) );
        final BlobRecord accessControlRecord =
            dumpReader.getRecord( accessControlSegment, BlobKey.from( versionDumpEntryJson.getAccessControlBlobKey() ) );

        try
        {
            return JsonDumpSerializer.toNodeStoreVersion( nodeRecord.getBytes(), indexConfigRecord.getBytes(),
                                                          accessControlRecord.getBytes() );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read node version [" + nodeRecord.getKey() + "]", e );
        }
    }

    private BlobKey writeNodeStoreVersionBlob( final NodeStoreVersion nodeVersion, final Segment segment,
                                               final NodeStoreVersionSerializer serializer )
    {
        try
        {
            return dumpWriter.addBlobRecord( segment, ByteSource.wrap( serializer.serialize( nodeVersion ) ) );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot write blob for segment [" + segment + "]", e );
        }
    }

    private List<AdditionalDumpEntry> createAdditionalNodes( final RepositoryId repositoryId )
    {
        final List<NewDumpNode> additionalNodes =
            SystemConstants.SYSTEM_REPO_ID.equals( repositoryId ) ? new DefaultProjectRolesCreator().createRoleNodes() : List.of();
        if ( additionalNodes.isEmpty() )
        {
            return List.of();
        }

        final Segment nodeSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.NODE_SEGMENT_LEVEL );
        final Segment indexConfigSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL );
        final Segment accessControlSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL );

        final List<AdditionalDumpEntry> additionalEntries = new ArrayList<>();

        dumpWriter.openVersionsMeta( repositoryId );
        try
        {
            for ( NewDumpNode newNode : additionalNodes )
            {
                final BlobKey nodeBlobKey =
                    writeNodeStoreVersionBlob( newNode.nodeVersion(), nodeSegment, NodeVersionJsonSerializer::toNodeVersionBytes );
                final BlobKey indexConfigBlobKey = writeNodeStoreVersionBlob( newNode.nodeVersion(), indexConfigSegment,
                                                                              NodeVersionJsonSerializer::toIndexConfigDocumentBytes );
                final BlobKey accessControlBlobKey = writeNodeStoreVersionBlob( newNode.nodeVersion(), accessControlSegment,
                                                                                NodeVersionJsonSerializer::toAccessControlBytes );

                final VersionDumpEntryJson versionEntry = VersionDumpEntryJson.create()
                    .nodePath( newNode.nodePath() )
                    .timestamp( Instant.now().toString() )
                    .version( UUID.randomUUID().toString() )
                    .nodeBlobKey( nodeBlobKey.toString() )
                    .indexConfigBlobKey( indexConfigBlobKey.toString() )
                    .accessControlBlobKey( accessControlBlobKey.toString() )
                    .build();

                final VersionsDumpEntryJson versionsEntry =
                    VersionsDumpEntryJson.create().nodeId( newNode.nodeId() ).version( versionEntry ).build();

                dumpWriter.writeRawEntry( newNode.nodeId() + ".json", JsonDumpSerializer.serialize( versionsEntry ) );

                final BranchDumpEntryJson branchEntry =
                    BranchDumpEntryJson.create().nodeId( newNode.nodeId() ).meta( versionEntry ).build();

                additionalEntries.add( new AdditionalDumpEntry( branchEntry, newNode.branches() ) );

                LOG.info( "Created additional node [{}] at [{}] in repository [{}]", newNode.nodeId(), newNode.nodePath(), repositoryId );
            }
        }
        finally
        {
            dumpWriter.closeMeta();
        }

        return additionalEntries;
    }

    @FunctionalInterface
    private interface NodeStoreVersionSerializer
    {
        byte[] serialize( NodeStoreVersion nodeVersion )
            throws IOException;
    }

    protected @Nullable NodeStoreVersion upgradeNodeVersion( RepositoryId repositoryId, final NodeStoreVersion dumpEntry )
    {
        NodeStoreVersion result = dumpEntry;
        for ( NodeVersionUpgrader upgrader : List.of( new ContentUpgrader(), new AuditLogMillisUpgrader(), new SchedulerUpgrader(),
                                                      new ReferenceLowercaseUpgrader(), new DefaultProjectPermissionsUpgrader(),
                                                      new LanguageTagUpgrader(), new IndexConfigLanguageUpgrader(),
                                                      new AttachmentSha512Upgrader( dumpReader ), new RepositoryBranchesRemovalUpgrader(),
                                                      new RepositoryModelVersionUpgrader() ) )
        {
            final NodeStoreVersion upgraded = upgrader.upgradeNodeVersion( repositoryId, dumpEntry );
            if ( upgraded != null )
            {
                result = upgraded;
            }
        }
        return result;
    }

    @Override
    public Version getModelVersion()
    {
        return MODEL_VERSION;
    }

    public void processEntries( final BiConsumer<byte[], String> processor, final PathRef tarFile )
    {
        try (TarArchiveInputStream tarInputStream = dumpReader.openTarStream( tarFile ))
        {
            TarArchiveEntry entry = tarInputStream.getNextEntry();
            while ( entry != null )
            {
                processor.accept( tarInputStream.readAllBytes(), entry.getName() );
                entry = tarInputStream.getNextEntry();
            }
        }
        catch ( IOException e )
        {
            throw new RepoDumpException( "Cannot read meta-data", e );
        }
    }
}
