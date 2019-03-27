package com.enonic.xp.repo.impl.dump.upgrade.htmlarea;

import java.io.IOException;
import java.nio.file.Path;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.dump.DumpBlobRecord;
import com.enonic.xp.repo.impl.dump.serializer.json.BranchDumpEntryJson;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.AbstractMetaBlobUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.DumpUpgradeException;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.json.IndexConfigDocumentJson;
import com.enonic.xp.repo.impl.node.json.NodeVersionDataJson;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.util.Version;

public class HtmlAreaDumpUpgrader
    extends AbstractMetaBlobUpgrader
{

    private static final Segment NODE_SEGMENT =
        RepositorySegmentUtils.toSegment( ContentConstants.CONTENT_REPO_ID, NodeConstants.NODE_SEGMENT_LEVEL );

    private static final Segment INDEX_SEGMENT =
        RepositorySegmentUtils.toSegment( ContentConstants.CONTENT_REPO_ID, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL );

    private final HtmlAreaNodeDataUpgrader nodeDataUpgrader;

    public HtmlAreaDumpUpgrader( final Path basePath )
    {
        super( basePath );
        this.nodeDataUpgrader = new HtmlAreaNodeDataUpgrader();
    }

    @Override
    public Version getModelVersion()
    {
        return new Version( 8, 0, 0 );
    }

    protected void upgradeRepository( final RepositoryId repositoryId )
    {
        if ( ContentConstants.CONTENT_REPO_ID.equals( repositoryId ) )
        {
            super.upgradeRepository( repositoryId );
        }
    }

    @Override
    protected void upgradeVersionEntry( final RepositoryId repositoryId, final String entryContent )
    {

    }

    @Override
    protected void upgradeBranchEntry( final RepositoryId repositoryId, final String entryContent )
    {
        final BranchDumpEntryJson branchDumpEntryJson = deserializeValue( entryContent, BranchDumpEntryJson.class );
        final VersionDumpEntryJson versionDumpEntryJson = branchDumpEntryJson.getMeta();

        upgradeVersionMeta( versionDumpEntryJson );
    }

    private void upgradeVersionMeta( final VersionDumpEntryJson versionDumpEntryJson )
    {
        final DumpBlobRecord nodeDumpBlobRecord = (DumpBlobRecord) dumpReader.getDumpBlobStore().
            getRecord( NODE_SEGMENT, BlobKey.from( versionDumpEntryJson.getNodeBlobKey() ) );
        final DumpBlobRecord indexDumpBlobRecord = (DumpBlobRecord) dumpReader.getDumpBlobStore().
            getRecord( INDEX_SEGMENT, BlobKey.from( versionDumpEntryJson.getIndexConfigBlobKey() ) );
        upgradeBlobRecord( nodeDumpBlobRecord, indexDumpBlobRecord );
    }

    private void upgradeBlobRecord( final DumpBlobRecord nodeDumpBlobRecord, final DumpBlobRecord indexDumpBlobRecord )
    {
        final NodeVersion nodeVersion = getNodeVersion( nodeDumpBlobRecord );
        final PatternIndexConfigDocument indexConfigDocument = getIndexConfigDocument( indexDumpBlobRecord );
        final boolean upgraded = upgradeNodeVersion( nodeVersion, indexConfigDocument );
        if ( upgraded )
        {
            writeNodeVersion( nodeVersion, nodeDumpBlobRecord );
        }
    }

    private NodeVersion getNodeVersion( final DumpBlobRecord dumpBlobRecord )
    {
        final CharSource charSource = dumpBlobRecord.getBytes().asCharSource( Charsets.UTF_8 );
        try
        {
            final NodeVersionDataJson nodeVersionDataJson = deserializeValue( charSource.read(), NodeVersionDataJson.class );
            return nodeVersionDataJson.fromJson().build();
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read node version [" + dumpBlobRecord.getKey() + "]", e );
        }
    }

    private PatternIndexConfigDocument getIndexConfigDocument( final DumpBlobRecord dumpBlobRecord )
    {
        final CharSource charSource = dumpBlobRecord.getBytes().asCharSource( Charsets.UTF_8 );
        try
        {
            final IndexConfigDocumentJson indexConfigDocumentJson = deserializeValue( charSource.read(), IndexConfigDocumentJson.class );
            return indexConfigDocumentJson.fromJson();
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read node version [" + dumpBlobRecord.getKey() + "]", e );
        }
    }

    private void writeNodeVersion( final NodeVersion nodeVersion, final DumpBlobRecord dumpBlobRecord )
    {
        final String serializedUpgradedNodeVersion = serialize( NodeVersionDataJson.toJson( nodeVersion ) );
        final ByteSource byteSource = ByteSource.wrap( serializedUpgradedNodeVersion.getBytes( Charsets.UTF_8 ) );
        try
        {

            byteSource.copyTo( dumpBlobRecord.getByteSink() );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot copy node version [" + dumpBlobRecord.getKey() + "]", e );
        }
    }

    private boolean upgradeNodeVersion( final NodeVersion nodeVersion, final PatternIndexConfigDocument indexConfigDocument )
    {
        return nodeDataUpgrader.upgrade( nodeVersion, indexConfigDocument );
    }
}
