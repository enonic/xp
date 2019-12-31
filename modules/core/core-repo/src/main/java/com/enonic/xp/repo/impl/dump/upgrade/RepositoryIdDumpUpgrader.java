package com.enonic.xp.repo.impl.dump.upgrade;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.dump.DumpBlobRecord;
import com.enonic.xp.repo.impl.dump.FileUtils;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.serializer.json.DumpMetaJsonSerializer;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre5.Pre5ContentConstants;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre6.Pre6BranchDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre6.Pre6VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre6.Pre6VersionsDumpEntryJson;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.json.NodeVersionDataJson;
import com.enonic.xp.repo.impl.node.json.NodeVersionJsonSerializer;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.util.Version;

public class RepositoryIdDumpUpgrader
    extends AbstractMetaDumpUpgrader
{
    private static final Version MODEL_VERSION = new Version( 5 );

    private static final String NAME = "CMS Repository renaming";

    private static final RepositoryId OLD_REPOSITORY_ID = Pre5ContentConstants.CONTENT_REPO_ID;

    private static final RepositoryId NEW_REPOSITORY_ID = ContentConstants.CONTENT_REPO_ID;

    private static final String OLD_REPOSITORY_FILE_NAME = OLD_REPOSITORY_ID + ".json";

    private static final Segment SEGMENT =
        RepositorySegmentUtils.toSegment( ContentConstants.CONTENT_REPO_ID, NodeConstants.NODE_SEGMENT_LEVEL );

    private static final NodeVersionJsonSerializer NODE_VERSION_JSON_SERIALIZER = NodeVersionJsonSerializer.create();

    public RepositoryIdDumpUpgrader( final Path basePath )
    {
        super( basePath );
    }

    @Override
    public Version getModelVersion()
    {
        return MODEL_VERSION;
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public void doUpgrade( final String dumpName )
    {
        super.doUpgrade( dumpName );

        upgradeRepositoryDir();
        upgradeDumpMetaFile();
    }

    @Override
    protected String upgradeVersionEntry( final RepositoryId repositoryId, final String entryContent )
    {
        final Pre6VersionsDumpEntryJson sourceVersionsEntry = deserializeValue( entryContent, Pre6VersionsDumpEntryJson.class );

        final Collection<Pre6VersionDumpEntryJson> upgradedVersionList = sourceVersionsEntry.getVersions().
            stream().
            map( this::upgradeVersionDumpEntry ).
            collect( Collectors.toList() );

        final Pre6VersionsDumpEntryJson upgradedVersionsEntry = Pre6VersionsDumpEntryJson.create().
            nodeId( upgradeString( sourceVersionsEntry.getNodeId() ) ).
            versions( upgradedVersionList ).
            build();

        return serialize( upgradedVersionsEntry );
    }

    @Override
    protected String upgradeBranchEntry( final RepositoryId repositoryId, final String entryContent )
    {
        final Pre6BranchDumpEntryJson sourceBranchEntry = deserializeValue( entryContent, Pre6BranchDumpEntryJson.class );
        final Pre6VersionDumpEntryJson sourceVersionEntry = sourceBranchEntry.getMeta();

        final Pre6VersionDumpEntryJson updatedVersionEntry = upgradeVersionDumpEntry( sourceVersionEntry );

        final Pre6BranchDumpEntryJson updatedBranchEntry = Pre6BranchDumpEntryJson.create( sourceBranchEntry ).
            nodeId( upgradeString( sourceBranchEntry.getNodeId() ) ).
            meta( updatedVersionEntry ).
            build();

        return serialize( updatedBranchEntry );
    }

    private Pre6VersionDumpEntryJson upgradeVersionDumpEntry( final Pre6VersionDumpEntryJson sourceVersionEntry )
    {
        final Pre6VersionDumpEntryJson updatedVersionEntry = Pre6VersionDumpEntryJson.create( sourceVersionEntry ).
            nodePath( upgradeString( sourceVersionEntry.getNodePath() ) ).
            build();

        upgradeNodeVersionBlob( updatedVersionEntry.getNodeBlobKey() );

        return updatedVersionEntry;
    }

    private void upgradeNodeVersionBlob( final String nodeBlobKey )
    {
        final DumpBlobRecord dumpBlobRecord = (DumpBlobRecord) dumpReader.getDumpBlobStore().
            getRecord( SEGMENT, BlobKey.from( nodeBlobKey ) );

        final NodeVersionDataJson sourceNodeVersion = getNodeVersion( dumpBlobRecord );

        final NodeVersion updatedNodeVersion = sourceNodeVersion.fromJson().
            id( NodeId.from( upgradeString( sourceNodeVersion.getId() ) ) ).
            build();

        writeNodeVersion( updatedNodeVersion, dumpBlobRecord );
    }

    private void upgradeRepositoryDir()
    {
        final Path oldRepoDirectory = this.dumpReader.getRepositoryDir( OLD_REPOSITORY_ID );

        if ( oldRepoDirectory != null )
        {
            final Path newRepoDirectory = oldRepoDirectory.getParent().resolve( NEW_REPOSITORY_ID.toString() );

            try
            {
                FileUtils.moveDirectory( oldRepoDirectory, newRepoDirectory );
            }
            catch ( IOException e )
            {
                throw new DumpUpgradeException(
                    String.format( "Cannot rename repository folder from '%s' to '%s'", OLD_REPOSITORY_ID, NEW_REPOSITORY_ID ), e );
            }
        }
    }

    private void upgradeDumpMetaFile()
    {
        final DumpMeta sourceDumpMeta = dumpReader.getDumpMeta();

        final DumpMeta upgradedDumpMeta = DumpMeta.create( sourceDumpMeta ).
            systemDumpResult( upgradeSystemDumpResult( sourceDumpMeta.getSystemDumpResult() ) ).
            build();

        final Path dumpMetaFile = dumpReader.getMetaDataFile();

        try
        {
            Files.writeString( dumpMetaFile, new DumpMetaJsonSerializer().serialize( upgradedDumpMeta ) );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Unable to upgrade dump meta file: " + dumpMetaFile.getFileName(), e );
        }
    }

    private SystemDumpResult upgradeSystemDumpResult( final SystemDumpResult sourceSystemDumpResult )
    {
        final SystemDumpResult.Builder upgradedSystemDumpResult = SystemDumpResult.create();

        sourceSystemDumpResult.stream().
            map( this::upgradeRepoDumpResult ).
            forEach( upgradedSystemDumpResult::add );

        return upgradedSystemDumpResult.build();
    }

    private RepoDumpResult upgradeRepoDumpResult( final RepoDumpResult sourceRepoDumpResult )
    {
        return RepoDumpResult.
            create( sourceRepoDumpResult ).
            repositoryId( upgradeRepositoryId( sourceRepoDumpResult.getRepositoryId() ) ).
            build();
    }


    @Override
    protected void upgradeRepository( final RepositoryId repositoryId )
    {
        if ( SystemConstants.SYSTEM_REPO_ID.equals( repositoryId ) )
        {
            super.upgradeRepository( repositoryId );
        }
    }

    @Override
    protected void upgradeBranch( final RepositoryId repositoryId, final Branch branch )
    {
        if ( ContentConstants.BRANCH_MASTER.equals( branch ) )
        {
            super.upgradeBranch( repositoryId, branch );
        }
    }

    @Override
    protected void upgradeBranchEntries( final RepositoryId repositoryId, final Branch branch, final Path entriesFile )
    {
        super.upgradeBranchEntries( repositoryId, branch, entriesFile );
    }

    @Override
    protected boolean hasToUpgradeEntry( final RepositoryId repositoryId, final String entryContent, final String entryName )
    {
        return OLD_REPOSITORY_FILE_NAME.equals( entryName );
    }

    @Override
    protected String upgradeEntryName( final RepositoryId repositoryId, final String entryName )
    {
        return upgradeString( entryName );
    }

    private RepositoryId upgradeRepositoryId( final RepositoryId source )
    {
        return RepositoryId.from( upgradeString( source.toString() ) );
    }

    private String upgradeString( final String source )
    {
        return source.replace( OLD_REPOSITORY_ID.toString(), NEW_REPOSITORY_ID.toString() );
    }

    private NodeVersionDataJson getNodeVersion( final DumpBlobRecord dumpBlobRecord )
    {
        final CharSource charSource = dumpBlobRecord.getBytes().asCharSource( StandardCharsets.UTF_8 );
        try
        {
            return deserializeValue( charSource.read(), NodeVersionDataJson.class );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read node version [" + dumpBlobRecord.getKey() + "]", e );
        }
    }

    private void writeNodeVersion( final NodeVersion nodeVersion, final DumpBlobRecord dumpBlobRecord )
    {
        final String serializedUpgradedNodeVersion = NODE_VERSION_JSON_SERIALIZER.toNodeString( nodeVersion );
        final ByteSource byteSource = ByteSource.wrap( serializedUpgradedNodeVersion.getBytes( StandardCharsets.UTF_8 ) );
        try
        {
            byteSource.copyTo( dumpBlobRecord.getByteSink() );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot copy node version [" + dumpBlobRecord.getKey() + "]", e );
        }
    }
}
