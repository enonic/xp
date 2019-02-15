package com.enonic.xp.repo.impl.dump.upgrade;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.repo.impl.dump.serializer.json.BranchDumpEntryJson;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionsDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageIndexUpgrader;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.node.json.IndexConfigDocumentJson;
import com.enonic.xp.repo.impl.node.json.NodeVersionDataJson;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.util.Version;

import static com.enonic.xp.content.ContentPropertyNames.LANGUAGE;

public class IndexConfigUpgrader
    extends AbstractMetaDumpUpgrader
{
    private static final Segment INDEX_CONFIG_SEGMENT =
        RepositorySegmentUtils.toSegment( ContentConstants.CONTENT_REPO_ID, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL );

    private static final Segment NODE_SEGMENT =
        RepositorySegmentUtils.toSegment( ContentConstants.CONTENT_REPO_ID, NodeConstants.NODE_SEGMENT_LEVEL );


    public IndexConfigUpgrader( final Path basePath )
    {
        super( basePath );
    }

    @Override
    public Version getModelVersion()
    {
        return new Version( 7, 0, 0 );
    }

    protected void upgradeRepository( final RepositoryId repositoryId )
    {
        if ( ContentConstants.CONTENT_REPO_ID.equals( repositoryId ) )
        {
            super.upgradeRepository( repositoryId );
        }
    }

    @Override
    protected String upgradeBranchEntry( final RepositoryId repositoryId, final String entryContent )
    {
        final BranchDumpEntryJson sourceBranchEntry = deserializeValue( entryContent, BranchDumpEntryJson.class );
        final VersionDumpEntryJson sourceVersionEntry = sourceBranchEntry.getMeta();

        final VersionDumpEntryJson upgradedVersionEntry = upgradeVersionMeta( sourceVersionEntry );

        if ( !sourceVersionEntry.equals( upgradedVersionEntry ) )
        {
            return serialize( BranchDumpEntryJson.create( sourceBranchEntry ).meta( upgradedVersionEntry ).build() );
        }

        return entryContent;
    }

    @Override
    protected String upgradeVersionEntry( final RepositoryId repositoryId, final String entryContent )
    {
        final VersionsDumpEntryJson sourceVersionsEntry = deserializeValue( entryContent, VersionsDumpEntryJson.class );

        final Collection<VersionDumpEntryJson> upgradedVersionList = sourceVersionsEntry.getVersions().
            stream().
            map( this::upgradeVersionMeta ).
            collect( Collectors.toList() );

        final VersionsDumpEntryJson upgradedVersionsEntry = VersionsDumpEntryJson.create().
            nodeId( sourceVersionsEntry.getNodeId() ).
            versions( upgradedVersionList ).
            build();

        return !sourceVersionsEntry.equals( upgradedVersionsEntry ) ? serialize( upgradedVersionsEntry ) : entryContent;
    }


    private VersionDumpEntryJson upgradeVersionMeta( final VersionDumpEntryJson version )
    {
        final BlobRecord nodeBlobRecord = dumpReader.getDumpBlobStore().
            getRecord( NODE_SEGMENT, BlobKey.from( version.getNodeBlobKey() ) );

        final NodeVersionDataJson nodeVersionDataJson = getNode( nodeBlobRecord );

        final NodeVersion nodeVersion = nodeVersionDataJson.fromJson().build();

        final BlobKey newIndexConfigBlob = upgradeIndexConfigDocument( BlobKey.from( version.getIndexConfigBlobKey() ), nodeVersion );

        return VersionDumpEntryJson.create( version ).
            indexConfigBlobKey( newIndexConfigBlob.toString() ).
            build();

    }

    private NodeVersionDataJson getNode( final BlobRecord nodeBlobRecord )
    {
        final CharSource charSource = nodeBlobRecord.getBytes().asCharSource( Charsets.UTF_8 );
        try
        {
            return deserializeValue( charSource.read(), NodeVersionDataJson.class );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read node [" + nodeBlobRecord.getKey() + "]", e );
        }
    }

    private PatternIndexConfigDocument getIndexConfigDocument( final BlobRecord indexConfigBlobRecord )
    {
        final CharSource charSource = indexConfigBlobRecord.getBytes().asCharSource( Charsets.UTF_8 );
        try
        {
            final IndexConfigDocumentJson indexConfigDocumentJson = deserializeValue( charSource.read(), IndexConfigDocumentJson.class );
            return indexConfigDocumentJson.fromJson();
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read index config [" + indexConfigBlobRecord.getKey() + "]", e );
        }
    }

    private BlobKey upgradeIndexConfigDocument( final BlobKey blobKey, final NodeVersion nodeVersion )
    {
        final BlobRecord indexConfigBlobRecord = dumpReader.getDumpBlobStore().
            getRecord( INDEX_CONFIG_SEGMENT, blobKey );

        final PatternIndexConfigDocument sourceDocument = getIndexConfigDocument( indexConfigBlobRecord );

        PatternIndexConfigDocument upgradedDocument = this.upgradeLanguageIndexConfig( sourceDocument, nodeVersion );
        upgradedDocument = this.upgradePageIndexConfig( upgradedDocument, nodeVersion );

        return !upgradedDocument.equals( sourceDocument ) ? storeIndexConfigBlob( upgradedDocument ) : blobKey;
    }

    private PatternIndexConfigDocument upgradeLanguageIndexConfig( final PatternIndexConfigDocument sourceDocument,
                                                                   final NodeVersion nodeVersion )
    {
        final String language = nodeVersion.getData().getString( LANGUAGE );
        if ( language != null )
        {
            final String normalizedLanguage = Locale.forLanguageTag( language ).getLanguage();

            return PatternIndexConfigDocument.create( sourceDocument ).addAllTextConfigLanguage( normalizedLanguage ).build();
        }
        return sourceDocument;
    }

    private PatternIndexConfigDocument upgradePageIndexConfig( final PatternIndexConfigDocument sourceDocument,
                                                               final NodeVersion nodeVersion )
    {

        final List<PropertySet> components = Lists.newArrayList( nodeVersion.getData().getSets( "components" ) );

        if ( components.size() == 0 )
        {
            return sourceDocument;
        }

        final String descriptorKeyStr = nodeVersion.getData().getString( PropertyPath.from( "components.page.descriptor" ) );
        final DescriptorKey pageDescriptorKey = descriptorKeyStr != null ? DescriptorKey.from( descriptorKeyStr ) : null;

        if ( pageDescriptorKey == null )
        {
            return sourceDocument;
        }
        final FlattenedPageIndexUpgrader pageIndexUpgrader = new FlattenedPageIndexUpgrader( pageDescriptorKey, components );

        if ( pageIndexUpgrader.needAnUpgrade( sourceDocument ) )
        {
            return pageIndexUpgrader.upgrade( sourceDocument );
        }

        return sourceDocument;
    }

    private BlobKey storeIndexConfigBlob( final PatternIndexConfigDocument indexConfigDocument )
    {
        final IndexConfigDocumentJson indexConfigDocumentJson = IndexConfigDocumentJson.toJson( indexConfigDocument );

        final String indexConfigDocumentSerialized = serialize( indexConfigDocumentJson );

        final BlobRecord indexBlobRecord =
            dumpReader.getDumpBlobStore().addRecord( INDEX_CONFIG_SEGMENT, ByteSource.wrap( indexConfigDocumentSerialized.getBytes() ) );

        return indexBlobRecord.getKey();
    }
}
