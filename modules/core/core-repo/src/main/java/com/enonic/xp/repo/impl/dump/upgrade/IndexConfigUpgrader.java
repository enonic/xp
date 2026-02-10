package com.enonic.xp.repo.impl.dump.upgrade;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.io.CharSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.index.AllTextIndexConfig;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.blobstore.DumpBlobRecord;
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
    private static final RepositoryId CONTENT_REPO_ID = RepositoryId.from( "com.enonic.cms.default" );

    private static final Version MODEL_VERSION = new Version( 7 );

    private static final String NAME = "Index config";

    private static final Segment INDEX_CONFIG_SEGMENT =
        RepositorySegmentUtils.toSegment( CONTENT_REPO_ID, NodeConstants.INDEX_CONFIG_SEGMENT_LEVEL );

    private static final Segment NODE_SEGMENT =
        RepositorySegmentUtils.toSegment( CONTENT_REPO_ID, NodeConstants.NODE_SEGMENT_LEVEL );


    public IndexConfigUpgrader( final Path basePath )
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
    protected void upgradeRepository( final RepositoryId repositoryId )
    {
        if ( CONTENT_REPO_ID.equals( repositoryId ) )
        {
            super.upgradeRepository( repositoryId );
        }
    }

    @Override
    protected byte[] upgradeBranchEntry( final RepositoryId repositoryId, final String entryContent )
    {
        final BranchDumpEntryJson sourceBranchEntry = deserializeValue( entryContent, BranchDumpEntryJson.class );
        final VersionDumpEntryJson sourceVersionEntry = sourceBranchEntry.getMeta();

        final VersionDumpEntryJson upgradedVersionEntry = upgradeVersionMeta( sourceVersionEntry );

        if ( !sourceVersionEntry.equals( upgradedVersionEntry ) )
        {
            return serialize( BranchDumpEntryJson.create( sourceBranchEntry ).meta( upgradedVersionEntry ).build() );
        }

        return entryContent.getBytes( StandardCharsets.UTF_8 );
    }

    @Override
    protected byte[] upgradeVersionEntry( final RepositoryId repositoryId, final String entryContent )
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

        return !sourceVersionsEntry.equals( upgradedVersionsEntry )
            ? serialize( upgradedVersionsEntry )
            : entryContent.getBytes( StandardCharsets.UTF_8 );
    }


    private VersionDumpEntryJson upgradeVersionMeta( final VersionDumpEntryJson version )
    {
        final DumpBlobRecord nodeBlobRecord = dumpReader.getRecord( NODE_SEGMENT, BlobKey.from( version.getNodeBlobKey() ) );

        final NodeVersionDataJson nodeVersionDataJson = getNode( nodeBlobRecord );

        final NodeStoreVersion nodeVersion = NodeVersionDataJson.fromJson( nodeVersionDataJson );

        if ( ContentConstants.CONTENT_NODE_COLLECTION.equals( nodeVersion.nodeType() ) )
        {
            final BlobKey newIndexConfigBlob = upgradeIndexConfigDocument( BlobKey.from( version.getIndexConfigBlobKey() ), nodeVersion );

            return VersionDumpEntryJson.create( version ).
                indexConfigBlobKey( newIndexConfigBlob.toString() ).
                build();
        }

        return version;
    }

    private NodeVersionDataJson getNode( final DumpBlobRecord nodeBlobRecord )
    {
        final CharSource charSource = nodeBlobRecord.getBytes().asCharSource( StandardCharsets.UTF_8 );
        try
        {
            return deserializeValue( charSource.read(), NodeVersionDataJson.class );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read node [" + nodeBlobRecord.getKey() + "]", e );
        }
    }

    private PatternIndexConfigDocument getIndexConfigDocument( final DumpBlobRecord indexConfigBlobRecord )
    {
        final CharSource charSource = indexConfigBlobRecord.getBytes().asCharSource( StandardCharsets.UTF_8 );
        try
        {
            return IndexConfigDocumentJson.fromJson( deserializeValue( charSource.read(), IndexConfigDocumentJson.class ) );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read index config [" + indexConfigBlobRecord.getKey() + "]", e );
        }
    }

    private BlobKey upgradeIndexConfigDocument( final BlobKey blobKey, final NodeStoreVersion nodeVersion )
    {
        final DumpBlobRecord indexConfigBlobRecord = dumpReader.getRecord( INDEX_CONFIG_SEGMENT, blobKey );

        final PatternIndexConfigDocument sourceDocument = getIndexConfigDocument( indexConfigBlobRecord );

        PatternIndexConfigDocument upgradedDocument = this.upgradeLanguageIndexConfig( sourceDocument, nodeVersion );
        upgradedDocument = this.upgradePageIndexConfig( upgradedDocument, nodeVersion );

        return !upgradedDocument.equals( sourceDocument ) ? storeIndexConfigBlob( upgradedDocument ) : blobKey;
    }

    private PatternIndexConfigDocument upgradeLanguageIndexConfig( final PatternIndexConfigDocument sourceDocument,
                                                                   final NodeStoreVersion nodeVersion )
    {
        final String language = nodeVersion.data().getString( LANGUAGE );

        if ( language != null )
        {
            final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create( sourceDocument );
            final AllTextIndexConfig.Builder allTextBuilder = AllTextIndexConfig.create( sourceDocument.getAllTextConfig() );

            final String normalizedLanguage = Locale.forLanguageTag( language ).getLanguage();

            builder.allTextConfig( allTextBuilder.addLanguage( normalizedLanguage ).build() );
            builder.add( LANGUAGE, IndexConfig.NGRAM );

            return builder.build();
        }

        return sourceDocument;
    }

    private PatternIndexConfigDocument upgradePageIndexConfig( final PatternIndexConfigDocument sourceDocument,
                                                               final NodeStoreVersion nodeVersion )
    {
        final List<PropertySet> components = Lists.newArrayList( nodeVersion.data().getSets( "components" ) );

        final String descriptorKeyStr = nodeVersion.data().getString( PropertyPath.from( "components.page.descriptor" ) );
        final DescriptorKey pageDescriptorKey = descriptorKeyStr != null ? DescriptorKey.from( descriptorKeyStr ) : null;

        final FlattenedPageIndexUpgrader pageIndexUpgrader = new FlattenedPageIndexUpgrader( pageDescriptorKey, components );

        return pageIndexUpgrader.upgrade( sourceDocument );
    }

    private BlobKey storeIndexConfigBlob( final PatternIndexConfigDocument indexConfigDocument )
    {
        final IndexConfigDocumentJson indexConfigDocumentJson = IndexConfigDocumentJson.toJson( indexConfigDocument );

        final byte[] indexConfigDocumentSerialized = serialize( indexConfigDocumentJson );

        return dumpReader.addRecord( INDEX_CONFIG_SEGMENT, indexConfigDocumentSerialized );
    }
}
