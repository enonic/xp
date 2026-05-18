package com.enonic.xp.repo.impl.dump.upgrade.model8to9;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.dump.DumpUpgradeStepResult;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.reader.DumpReaderModel8;
import com.enonic.xp.repo.impl.dump.serializer.json.CommitDumpEntryJson;
import com.enonic.xp.repo.impl.dump.serializer.json.JsonDumpSerializer;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;
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

    private final DumpReaderModel8 dumpReader;

    private DumpWriter dumpWriter;

    private DumpUpgradeStepResult.Builder result;

    private final Map<String, String> blobKeyMapping = new HashMap<>();

    private final Set<String> droppedCommitIds = new HashSet<>();

    private final Map<String, VersionHistoryMigrationUpgrader.CommitInfo> commitInfos = new HashMap<>();

    private final Set<String> nodeIdsWithVersions = new HashSet<>();

    private final Map<String, List<VersionDumpEntryJson>> orphanSynthesizedVersions = new HashMap<>();

    /**
     * Per-repository: for each nodeId, which versionId is active in which v8 branches.
     * Populated in a pre-pass over the v8 branch tars; consulted while emitting v9 version JSONL lines.
     */
    private final Map<String, Map<String, List<String>>> branchActivations = new HashMap<>();

    /**
     * Per-repository: the v8 meta blob keyed by (nodeId, versionId). Used to materialize orphan versions
     * (nodes referenced by branches whose v8 versions tar entry is missing).
     */
    private final Map<String, Map<String, VersionDumpEntryJson>> v8MetasByNode = new HashMap<>();

    private boolean repoInScope;

    private List<NodeVersionEntryUpgrader> versionEntryUpgraders = List.of();

    private List<BranchEntryUpgrader> branchEntryUpgraders = List.of();

    /**
     * Per-project legacy metadata harvested from the system repo's project config nodes (v8 stored
     * {@code displayName}/{@code description} there). Used to populate the project's {@code /content}
     * node and then stripped from the system repo entries by {@link ProjectMetadataStripperUpgrader}.
     */
    private final Map<RepositoryId, ProjectContentRootMetadataUpgrader.ProjectMetadata> projectMetadata = new HashMap<>();

    private final ProjectContentRootMetadataUpgrader contentRootMetadataUpgrader =
        new ProjectContentRootMetadataUpgrader( projectMetadata );

    public DumpUpgrader8to9( final DumpReaderModel8 dumpReader )
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

        collectProjectMetadata();

        this.dumpReader.getRepositories().forEach( this::upgradeRepository );
        return result.build();
    }

    /**
     * Pre-pass over the system repo's v8 versions tar to harvest per-project {@code displayName}
     * and {@code description}, which v8 stored on the project's repository config node. The values
     * are written onto the project's {@code /content} node by {@link ProjectContentRootMetadataUpgrader}
     * during the per-repo loop, and removed from the system repo entries by
     * {@link ProjectMetadataStripperUpgrader}.
     */
    private void collectProjectMetadata()
    {
        final RepositoryId systemRepoId = SystemConstants.SYSTEM_REPO_ID;
        final Segment nodeSegment = RepositorySegmentUtils.toSegment( systemRepoId, NodeConstants.NODE_SEGMENT_LEVEL );
        final Segment indexConfigSegment = RepositorySegmentUtils.toSegment( systemRepoId, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL );
        final Segment accessControlSegment = RepositorySegmentUtils.toSegment( systemRepoId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL );

        dumpReader.getVersions( systemRepoId ).ifPresent( ref -> processEntries( ( entryContent, entryName ) -> {
            try
            {
                final Model8VersionsDumpEntryJson v8Entry = JsonDumpSerializer.readValue( entryContent, Model8VersionsDumpEntryJson.class );
                for ( VersionDumpEntryJson version : v8Entry.getVersions() )
                {
                    final NodeStoreVersion nv = readNodeVersion( version, nodeSegment, indexConfigSegment, accessControlSegment );
                    extractProjectMetadata( nv );
                }
            }
            catch ( Exception e )
            {
                LOG.error( "Error while pre-scanning system repo entry [{}] for project metadata", entryName, e );
            }
        }, ref ) );
    }

    private void extractProjectMetadata( final NodeStoreVersion nodeVersion )
    {
        final PropertyTree data = nodeVersion.data();
        if ( !data.hasProperty( "data" ) )
        {
            return;
        }
        final PropertySet repoData = data.getSet( "data" );
        if ( repoData == null )
        {
            return;
        }
        final PropertySet projectData = repoData.getSet( ProjectConstants.PROJECT_DATA_SET_NAME );
        if ( projectData == null )
        {
            return;
        }
        final String repoIdValue = data.getString( "id" );
        if ( repoIdValue == null )
        {
            return;
        }
        final RepositoryId repoId = RepositoryId.from( repoIdValue );
        if ( !repoId.toString().startsWith( ProjectConstants.PROJECT_REPO_ID_PREFIX ) )
        {
            return;
        }
        final String displayName = projectData.getString( ProjectConstants.PROJECT_DISPLAY_NAME_PROPERTY );
        final String description = projectData.getString( ProjectConstants.PROJECT_DESCRIPTION_PROPERTY );
        if ( displayName != null || description != null )
        {
            projectMetadata.put( repoId, new ProjectContentRootMetadataUpgrader.ProjectMetadata( displayName, description ) );
        }
    }

    protected void upgradeRepository( final RepositoryId repositoryId )
    {
        blobKeyMapping.clear();
        droppedCommitIds.clear();
        commitInfos.clear();
        nodeIdsWithVersions.clear();
        orphanSynthesizedVersions.clear();
        branchActivations.clear();
        v8MetasByNode.clear();

        repoInScope = repositoryId.toString().startsWith( ProjectConstants.PROJECT_REPO_ID_PREFIX );
        final NodePathTrimUpgrader nodePathTrimUpgrader = new NodePathTrimUpgrader();
        final List<NodeVersionEntryUpgrader> versionUpgraders = new ArrayList<>();
        final List<BranchEntryUpgrader> branchUpgraders = new ArrayList<>();
        if ( repoInScope )
        {
            branchUpgraders.add( new VersionHistoryMigrationUpgrader() );
        }
        versionUpgraders.add( nodePathTrimUpgrader );
        branchUpgraders.add( nodePathTrimUpgrader );
        versionEntryUpgraders = List.copyOf( versionUpgraders );
        branchEntryUpgraders = List.copyOf( branchUpgraders );

        dumpReader.getCommits( repositoryId ).ifPresent( ref -> upgradeCommitEntries( repositoryId, ref ) );

        // Pre-pass: read v8 branch tar entries to know which versions are active in which branches.
        for ( Branch branch : dumpReader.getBranches( repositoryId ) )
        {
            dumpReader.getBranchEntries( repositoryId, branch ).ifPresent( ref -> collectBranchActivations( ref, branch ) );
        }

        // Process v8 versions tar, emitting v9 JSONL with branches inline.
        dumpReader.getVersions( repositoryId ).ifPresent( ref -> upgradeVersionEntries( repositoryId, ref ) );

        // Materialize default project roles (or other synthetic nodes) as additional version entries.
        createAdditionalNodes( repositoryId );

        // Emit synthesized versions for nodes referenced by branches but absent from the v8 versions tar.
        writeSynthesizedOrphanVersions( repositoryId );
    }

    private void collectBranchActivations( final PathRef entriesFile, final Branch branch )
    {
        processEntries( ( entryContent, entryName ) -> {
            try
            {
                final Model8BranchDumpEntryJson v8Entry = JsonDumpSerializer.readValue( entryContent, Model8BranchDumpEntryJson.class );
                final String nodeId = v8Entry.getNodeId();
                final VersionDumpEntryJson v8Meta = v8Entry.getMeta();
                final String existing = v8Meta.getVersion();
                final String versionId = existing != null ? existing : new NodeVersionId().toString();
                final VersionDumpEntryJson metaWithVersionId =
                    existing != null ? v8Meta : VersionDumpEntryJson.create( v8Meta ).version( versionId ).build();
                branchActivations.computeIfAbsent( nodeId, _ -> new LinkedHashMap<>() )
                    .computeIfAbsent( versionId, _ -> new ArrayList<>() )
                    .add( branch.getValue() );
                v8MetasByNode.computeIfAbsent( nodeId, _ -> new LinkedHashMap<>() ).putIfAbsent( versionId, metaWithVersionId );
            }
            catch ( Exception e )
            {
                LOG.error( "Error while reading v8 branch entry [{}]", entryName, e );
            }
        }, entriesFile );
    }

    private void writeSynthesizedOrphanVersions( final RepositoryId repositoryId )
    {
        dumpWriter.openVersionsMeta( repositoryId );
        try
        {
            for ( Map.Entry<String, Map<String, VersionDumpEntryJson>> nodeEntry : v8MetasByNode.entrySet() )
            {
                final String nodeId = nodeEntry.getKey();
                if ( nodeIdsWithVersions.contains( nodeId ) )
                {
                    continue;
                }
                final List<VersionDumpEntryJson> synthesized = new ArrayList<>();
                for ( Map.Entry<String, VersionDumpEntryJson> versionEntry : nodeEntry.getValue().entrySet() )
                {
                    synthesized.add( upgradeOrphanMeta( repositoryId, versionEntry.getValue(), versionEntry.getKey() ) );
                }
                writeVersionsJsonl( nodeId, synthesized );
            }
        }
        finally
        {
            dumpWriter.closeMeta();
        }
    }

    private void writeVersionsJsonl( final String nodeId, final List<VersionDumpEntryJson> versions )
    {
        final Map<String, List<String>> activationsForNode = branchActivations.getOrDefault( nodeId, Map.of() );
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        for ( VersionDumpEntryJson version : versions )
        {
            try
            {
                if ( out.size() > 0 )
                {
                    out.write( '\n' );
                }
                final List<String> branches = activationsForNode.getOrDefault( version.getVersion(), List.of() );
                final VersionDumpEntryJson.Builder lineBuilder = VersionDumpEntryJson.create( version ).nodeId( nodeId );
                if ( !branches.isEmpty() )
                {
                    lineBuilder.branches( branches );
                }
                out.write( JsonDumpSerializer.serialize( lineBuilder.build() ) );
            }
            catch ( IOException e )
            {
                throw new RepoDumpException( "Cannot write versions JSONL for node [" + nodeId + "]", e );
            }
        }
        dumpWriter.writeRawEntry( nodeId + ".jsonl", out.toByteArray() );
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
                    processVersionEntry( repositoryId, entryContent );
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
                if ( LayerBaseCommitDropUpgrader.isLayerBaseCommit( commitEntry ) )
                {
                    droppedCommitIds.add( commitEntry.getCommitId() );
                    return;
                }
                commitInfos.put( commitEntry.getCommitId(),
                                 new VersionHistoryMigrationUpgrader.CommitInfo( commitEntry.getMessage(), commitEntry.getCommitter(),
                                                                                 commitEntry.getTimestamp() ) );
                final CommitDumpEntryJson upgraded = CommitDumpEntryJson.from( CommitDumpEntryJson.fromJson( commitEntry ) );
                dumpWriter.writeRawEntry( entryName, JsonDumpSerializer.serialize( upgraded ) );
            }, entriesFile );
        }
        finally
        {
            dumpWriter.closeMeta();
        }
    }

    private void processVersionEntry( final RepositoryId repositoryId, final byte[] entryContent )
    {
        final Model8VersionsDumpEntryJson v8Entry = JsonDumpSerializer.readValue( entryContent, Model8VersionsDumpEntryJson.class );

        nodeIdsWithVersions.add( v8Entry.getNodeId() );

        final List<VersionDumpEntryJson> upgradedVersions = new ArrayList<>();
        for ( VersionDumpEntryJson versionDumpEntryJson : v8Entry.getVersions() )
        {
            upgradedVersions.add( ensureVersionId( processVersionMeta( versionDumpEntryJson, repositoryId ) ) );
        }

        writeVersionsJsonl( v8Entry.getNodeId(), upgradedVersions );
    }

    private static VersionDumpEntryJson ensureVersionId( final VersionDumpEntryJson entry )
    {
        if ( entry.getVersion() != null )
        {
            return entry;
        }
        return VersionDumpEntryJson.create( entry ).version( new NodeVersionId().toString() ).build();
    }

    private VersionDumpEntryJson processVersionMeta( final VersionDumpEntryJson versionDumpEntryJson, final RepositoryId repositoryId )
    {
        final Segment nodeSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.NODE_SEGMENT_LEVEL );
        final Segment indexConfigSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL );
        final Segment accessControlSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL );

        final NodeStoreVersion dumpEntry = readNodeVersion( versionDumpEntryJson, nodeSegment, indexConfigSegment, accessControlSegment );

        final NodePath entryPath = versionDumpEntryJson.getNodePath() != null ? new NodePath( versionDumpEntryJson.getNodePath() ) : null;
        contentRootMetadataUpgrader.upgrade( repositoryId, entryPath, dumpEntry );

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

        for ( NodeVersionEntryUpgrader upgrader : versionEntryUpgraders )
        {
            result = upgrader.upgradeVersionEntry( result );
        }

        if ( repoInScope )
        {
            result = VersionHistoryMigrationUpgrader.stampVersion( toWrite, result, commitInfos.get( result.getCommitId() ) );
        }

        return LayerBaseCommitDropUpgrader.clearDroppedCommitId( droppedCommitIds, result );
    }

    private VersionDumpEntryJson upgradeOrphanMeta( final RepositoryId repositoryId, final VersionDumpEntryJson v8Meta,
                                                    final String versionId )
    {
        final Segment nodeSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.NODE_SEGMENT_LEVEL );
        final Segment indexConfigSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL );
        final Segment accessControlSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL );

        final NodeStoreVersion nodeVersion = readNodeVersion( v8Meta, nodeSegment, indexConfigSegment, accessControlSegment );

        final VersionDumpEntryJson.Builder metaBuilder = VersionDumpEntryJson.create( v8Meta ).version( versionId );

        final String newNodeBlobKey = blobKeyMapping.get( v8Meta.getNodeBlobKey() );
        if ( newNodeBlobKey != null )
        {
            metaBuilder.nodeBlobKey( newNodeBlobKey );
        }

        final String newIndexConfigBlobKey = blobKeyMapping.get( v8Meta.getIndexConfigBlobKey() );
        if ( newIndexConfigBlobKey != null )
        {
            metaBuilder.indexConfigBlobKey( newIndexConfigBlobKey );
        }

        final String newAccessControlBlobKey = blobKeyMapping.get( v8Meta.getAccessControlBlobKey() );
        if ( newAccessControlBlobKey != null )
        {
            metaBuilder.accessControlBlobKey( newAccessControlBlobKey );
        }
        VersionDumpEntryJson result = metaBuilder.build();

        for ( BranchEntryUpgrader upgrader : branchEntryUpgraders )
        {
            result = upgrader.upgradeBranchMeta( nodeVersion, result );
        }

        return LayerBaseCommitDropUpgrader.clearDroppedCommitId( droppedCommitIds, result );
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

    private void createAdditionalNodes( final RepositoryId repositoryId )
    {
        final List<NewDumpNode> additionalNodes =
            SystemConstants.SYSTEM_REPO_ID.equals( repositoryId ) ? new DefaultProjectRolesCreator().createRoleNodes() : List.of();
        if ( additionalNodes.isEmpty() )
        {
            return;
        }

        final Segment nodeSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.NODE_SEGMENT_LEVEL );
        final Segment indexConfigSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL );
        final Segment accessControlSegment = RepositorySegmentUtils.toSegment( repositoryId, NodeConstants.ACCESS_CONTROL_SEGMENT_LEVEL );

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

                final String versionId = UUID.randomUUID().toString();
                final VersionDumpEntryJson versionEntry = VersionDumpEntryJson.create()
                    .nodePath( newNode.nodePath() )
                    .timestamp( Instant.now().toString() )
                    .version( versionId )
                    .nodeBlobKey( nodeBlobKey.toString() )
                    .indexConfigBlobKey( indexConfigBlobKey.toString() )
                    .accessControlBlobKey( accessControlBlobKey.toString() )
                    .build();

                // Register branch activations so writeVersionsJsonl emits this version with branches inline.
                for ( Branch branch : newNode.branches() )
                {
                    branchActivations.computeIfAbsent( newNode.nodeId(), _ -> new LinkedHashMap<>() )
                        .computeIfAbsent( versionId, _ -> new ArrayList<>() )
                        .add( branch.getValue() );
                }

                writeVersionsJsonl( newNode.nodeId(), List.of( versionEntry ) );
                nodeIdsWithVersions.add( newNode.nodeId() );

                LOG.info( "Created additional node [{}] at [{}] in repository [{}]", newNode.nodeId(), newNode.nodePath(), repositoryId );
            }
        }
        finally
        {
            dumpWriter.closeMeta();
        }
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
                                                      new AttachmentSha512Upgrader( dumpReader ), new AttachmentTextToMediaUpgrader(),
                                                      new ImageUpgrader( dumpReader ), new ProjectMetadataStripperUpgrader(),
                                                      new RepositoryBranchesRemovalUpgrader(), new RepositoryModelVersionUpgrader() ) )
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
