package com.enonic.xp.repo.impl.dump.upgrade.htmlarea;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import com.google.common.io.CharSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.dump.blobstore.DumpBlobRecord;
import com.enonic.xp.repo.impl.dump.serializer.json.BranchDumpEntryJson;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionsDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.AbstractMetaBlobUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.DumpUpgradeException;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.json.IndexConfigDocumentJson;
import com.enonic.xp.repo.impl.node.json.NodeVersionDataJson;
import com.enonic.xp.repo.impl.node.json.NodeVersionJsonSerializer;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.util.Version;

public class HtmlAreaDumpUpgrader
    extends AbstractMetaBlobUpgrader
{
    private static final RepositoryId DEFAULT_CONTENT_REPO_ID = RepositoryId.from( "com.enonic.cms.default" );

    private static final Version MODEL_VERSION = new Version( 8 );

    private static final String NAME = "Html Area";

    private static final Segment NODE_SEGMENT =
        RepositorySegmentUtils.toSegment( DEFAULT_CONTENT_REPO_ID, NodeConstants.NODE_SEGMENT_LEVEL );

    private static final Segment INDEX_SEGMENT =
        RepositorySegmentUtils.toSegment( DEFAULT_CONTENT_REPO_ID, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL );

    private final HtmlAreaNodeDataUpgrader nodeDataUpgrader;

    public HtmlAreaDumpUpgrader( final Path basePath )
    {
        super( basePath );
        this.nodeDataUpgrader = new HtmlAreaNodeDataUpgrader();
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
    protected void upgradeRepository( final RepositoryId repositoryId )
    {
        if ( DEFAULT_CONTENT_REPO_ID.equals( repositoryId ) )
        {
            super.upgradeRepository( repositoryId );
        }
    }

    @Override
    protected void upgradeVersionEntry( final RepositoryId repositoryId, final String entryContent )
    {
        final VersionsDumpEntryJson versionsDumpEntryJson = deserializeValue( entryContent, VersionsDumpEntryJson.class );
        versionsDumpEntryJson.getVersions().
            forEach(this::upgradeVersionMeta);
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
        final DumpBlobRecord nodeDumpBlobRecord = dumpReader.getRecord( NODE_SEGMENT, BlobKey.from( versionDumpEntryJson.getNodeBlobKey() ) );
        final DumpBlobRecord indexDumpBlobRecord = dumpReader.getRecord( INDEX_SEGMENT, BlobKey.from( versionDumpEntryJson.getIndexConfigBlobKey() ) );
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
        final CharSource charSource = dumpBlobRecord.getBytes().asCharSource( StandardCharsets.UTF_8 );
        try
        {
            final NodeVersionDataJson nodeVersionDataJson = deserializeValue( charSource.read(), NodeVersionDataJson.class );
            return NodeVersionDataJson.fromJson( nodeVersionDataJson );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read node version [" + dumpBlobRecord.getKey() + "]", e );
        }
    }

    private PatternIndexConfigDocument getIndexConfigDocument( final DumpBlobRecord dumpBlobRecord )
    {
        final CharSource charSource = dumpBlobRecord.getBytes().asCharSource( StandardCharsets.UTF_8 );
        try
        {
            return IndexConfigDocumentJson.fromJson( deserializeValue( charSource.read(), IndexConfigDocumentJson.class ) );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read node version [" + dumpBlobRecord.getKey() + "]", e );
        }
    }

    private void writeNodeVersion( final NodeVersion nodeVersion, final DumpBlobRecord dumpBlobRecord )
    {
        final byte[] serializedUpgradedNodeVersion = NodeVersionJsonSerializer.toNodeVersionBytes( nodeVersion ) ;
        try
        {
            dumpBlobRecord.override( serializedUpgradedNodeVersion );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot copy node version [" + dumpBlobRecord.getKey() + "]", e );
        }
    }

    private boolean upgradeNodeVersion( final NodeVersion nodeVersion, final PatternIndexConfigDocument indexConfigDocument )
    {
        return nodeDataUpgrader.upgrade( nodeVersion, indexConfigDocument, result );
    }
}
