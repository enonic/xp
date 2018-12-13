package com.enonic.xp.repo.impl.dump.upgrade.flattenedpage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.dump.DumpBlobRecord;
import com.enonic.xp.repo.impl.dump.DumpBlobStore;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;
import com.enonic.xp.repo.impl.dump.reader.FileDumpReader;
import com.enonic.xp.repo.impl.dump.serializer.json.JsonDumpSerializer;
import com.enonic.xp.repo.impl.dump.upgrade.DumpUpgradeException;
import com.enonic.xp.repo.impl.dump.upgrade.DumpUpgrader;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.json.NodeVersionJsonSerializer;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.util.Version;

public class FlattenedPageDumpUpgrader
    implements DumpUpgrader
{
    private final Path basePath;

    private FileDumpReader dumpReader;

    private DumpBlobStore dumpBlobStore;

    private JsonDumpSerializer jsonDumpSerializer;

    private final NodeVersionJsonSerializer serializer = NodeVersionJsonSerializer.create( false );

    private static final RepositoryId REPOSITORY_ID = ContentConstants.CONTENT_REPO_ID;

    private static final Segment SEGMENT =
        RepositorySegmentUtils.toSegment( ContentConstants.CONTENT_REPO_ID, NodeConstants.NODE_SEGMENT_LEVEL );

    public FlattenedPageDumpUpgrader( final Path basePath )
    {
        this.basePath = basePath;
    }

    @Override
    public Version getModelVersion()
    {
        return new Version( 3, 0, 0 );
    }

    @Override
    public void upgrade( final String dumpName )
    {
        this.dumpReader = new FileDumpReader( basePath, dumpName, null );
        this.dumpBlobStore = dumpReader.getDumpBlobStore();
        this.jsonDumpSerializer = new JsonDumpSerializer();

        final File versionsFile = this.dumpReader.getVersionsFile( REPOSITORY_ID );
        if ( versionsFile != null )
        {
            dumpReader.processEntries( ( entryContent, entryName ) -> {
                final VersionsDumpEntry sourceEntry = jsonDumpSerializer.toNodeVersionsEntry( entryContent );
                sourceEntry.getVersions().forEach( this::upgrade );
            }, versionsFile );
        }
        else
        {
            dumpReader.getBranches( REPOSITORY_ID ).forEach( this::upgrade );
        }
    }

    private void upgrade( final Branch branch )
    {
        final File branchEntriesFile = dumpReader.getBranchEntriesFile( REPOSITORY_ID, branch );
        if ( branchEntriesFile != null )
        {
            dumpReader.processEntries( ( entryContent, entryName ) -> {
                final VersionsDumpEntry sourceEntry = jsonDumpSerializer.toNodeVersionsEntry( entryContent );
                sourceEntry.getVersions().forEach( this::upgrade );
            }, branchEntriesFile );
        }
        else
        {
            throw new DumpUpgradeException( "Branch entries file missing for repository [" + REPOSITORY_ID + "] and branch [" + branch + "]" );
        }
    }

    private void upgrade( final VersionMeta versionMeta )
    {
        final DumpBlobRecord dumpBlobRecord = (DumpBlobRecord) dumpReader.getDumpBlobStore().
            getRecord( SEGMENT, versionMeta.getBlobKey() );
        upgrade( dumpBlobRecord );

    }

    private void upgrade( final DumpBlobRecord dumpBlobRecord )
    {
        final NodeVersion nodeVersion = getNodeVersion( dumpBlobRecord );
        final boolean upgraded = new FlattenedPageDataUpgrader().upgrade( nodeVersion.getData() );
        if ( upgraded )
        {
            writeNodeVersion( nodeVersion, dumpBlobRecord );
        }
    }

    private NodeVersion getNodeVersion( final DumpBlobRecord dumpBlobRecord )
    {
        final CharSource charSource = dumpBlobRecord.getBytes().asCharSource( Charsets.UTF_8 );
        try
        {
            return serializer.toNodeVersion( charSource.read() );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read node version [" + dumpBlobRecord.getKey() + "]", e );
        }
    }

    private void writeNodeVersion( final NodeVersion nodeVersion, final DumpBlobRecord dumpBlobRecord )
    {
        final String serializedUpgradedNodeVersion = serializer.toString( nodeVersion );
        final ByteSource byteSource = ByteSource.wrap( serializedUpgradedNodeVersion.getBytes( Charsets.UTF_8 ) );
        try
        {

            byteSource.copyTo( dumpBlobRecord.getByteSink() );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read node version [" + dumpBlobRecord.getKey() + "]", e );
        }
    }


}