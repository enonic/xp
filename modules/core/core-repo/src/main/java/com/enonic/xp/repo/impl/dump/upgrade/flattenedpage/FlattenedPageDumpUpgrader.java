package com.enonic.xp.repo.impl.dump.upgrade.flattenedpage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.dump.DumpBlobRecord;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.upgrade.AbstractDumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.DumpUpgradeException;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4.Pre4BranchDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4.Pre4NodeVersionJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4.Pre4VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4.Pre4VersionsDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre5.Pre5ContentConstants;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.util.Version;

public class FlattenedPageDumpUpgrader
    extends AbstractDumpUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( FlattenedPageDumpUpgrader.class );

    private static final Version MODEL_VERSION = new Version( 3 );

    private static final String NAME = "Flattened page";

    private static final RepositoryId REPOSITORY_ID = Pre5ContentConstants.CONTENT_REPO_ID;

    private static final Segment SEGMENT =
        RepositorySegmentUtils.toSegment( ContentConstants.CONTENT_REPO_ID, NodeConstants.NODE_SEGMENT_LEVEL );

    public FlattenedPageDumpUpgrader( final Path basePath )
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

        final Path versionsFile = dumpReader.getVersionsFile( REPOSITORY_ID );
        if ( versionsFile != null )
        {
            //Gathers the template -> controller mappings
            final TemplateControllerMappings templateControllerMappings = new TemplateControllerMappings();
            dumpReader.processEntries( ( entryContent, entryName ) -> {

                final Pre4VersionsDumpEntryJson sourceEntry = deserializeValue( entryContent, Pre4VersionsDumpEntryJson.class );
                final Pre4VersionDumpEntryJson sourceVersion = sourceEntry.getVersions().
                    stream().
                    findFirst().
                    get();
                addTemplateControllerMapping( sourceVersion, templateControllerMappings );
            }, versionsFile );
            final Map<String, String> templateControllerMap = templateControllerMappings.getMappings();

            //Update contents with pages
            final FlattenedPageDataUpgrader dataUpgrader = FlattenedPageDataUpgrader.create().
                templateControllerMap( templateControllerMap ).
                build();
            dumpReader.processEntries( ( entryContent, entryName ) -> {
                result.processed();
                try
                {
                    final Pre4VersionsDumpEntryJson sourceEntry = deserializeValue( entryContent, Pre4VersionsDumpEntryJson.class );
                    sourceEntry.getVersions().
                        forEach( version -> upgradeVersionMeta( version, dataUpgrader ) );
                }
                catch ( Exception e )
                {
                    result.error();
                    LOG.error( "Error while upgrading version entry [" + entryName + "]", e );
                }
            }, versionsFile );
        }
        else
        {
            dumpReader.getBranches( REPOSITORY_ID ).
                forEach( this::upgradeBranch );
        }
    }

    private String serializeValue( final NodeVersion nodeVersion )
    {
        try
        {
            return serialize( Pre4NodeVersionJson.toJson( nodeVersion ) );

        }
        catch ( final RepoDumpException e )
        {
            throw new RepoDumpException( "Cannot serialize node version [" + nodeVersion.toString() + "]", e );
        }
    }

    private void addTemplateControllerMapping( final Pre4VersionDumpEntryJson version,
                                               final TemplateControllerMappings templateControllerMapping )
    {
        final DumpBlobRecord dumpBlobRecord = (DumpBlobRecord) dumpReader.getDumpBlobStore().
            getRecord( SEGMENT, BlobKey.from( version.getBlobKey() ) );
        final NodeVersion nodeVersion = getNodeVersion( dumpBlobRecord );
        templateControllerMapping.handle( nodeVersion.getId(), nodeVersion.getData() );
    }

    private void upgradeBranch( final Branch branch )
    {
        final Path branchEntriesFile = dumpReader.getBranchEntriesFile( REPOSITORY_ID, branch );
        if ( branchEntriesFile != null )
        {
            //Gathers the template -> controller mappings
            final TemplateControllerMappings templateControllerMappings = new TemplateControllerMappings();
            dumpReader.processEntries( ( entryContent, entryName ) -> {

                final Pre4BranchDumpEntryJson sourceEntry = deserializeValue( entryContent, Pre4BranchDumpEntryJson.class );
                addTemplateControllerMapping( sourceEntry.getMeta(), templateControllerMappings );
            }, branchEntriesFile );
            final Map<String, String> templateControllerMap = templateControllerMappings.getMappings();

            //Update contents with pages
            final FlattenedPageDataUpgrader dataUpgrader = FlattenedPageDataUpgrader.create().
                templateControllerMap( templateControllerMap ).
                build();
            dumpReader.processEntries( ( entryContent, entryName ) -> {
                result.processed();
                try
                {
                    final Pre4BranchDumpEntryJson sourceEntry = deserializeValue( entryContent, Pre4BranchDumpEntryJson.class );
                    upgradeVersionMeta( sourceEntry.getMeta(), dataUpgrader );
                }
                catch ( Exception e )
                {
                    result.error();
                    LOG.error( "Error while upgrading branch entry [" + entryName + "]", e );
                }
            }, branchEntriesFile );
        }
        else
        {
            throw new DumpUpgradeException(
                "Branch entries file missing for repository [" + REPOSITORY_ID + "] and branch [" + branch + "]" );
        }
    }

    private void upgradeVersionMeta( final Pre4VersionDumpEntryJson version, final FlattenedPageDataUpgrader dataUpgrader )
    {
        final DumpBlobRecord dumpBlobRecord = (DumpBlobRecord) dumpReader.getDumpBlobStore().
            getRecord( SEGMENT, BlobKey.from( version.getBlobKey() ) );
        upgradeBlobRecord( dumpBlobRecord, dataUpgrader );

    }

    private void upgradeBlobRecord( final DumpBlobRecord dumpBlobRecord, final FlattenedPageDataUpgrader dataUpgrader )
    {
        final NodeVersion nodeVersion = getNodeVersion( dumpBlobRecord );
        final boolean upgraded = dataUpgrader.upgrade( nodeVersion.getData() );
        if ( upgraded )
        {
            writeNodeVersion( nodeVersion, dumpBlobRecord );
        }
    }

    private NodeVersion getNodeVersion( final DumpBlobRecord dumpBlobRecord )
    {
        final CharSource charSource = dumpBlobRecord.getBytes().asCharSource( StandardCharsets.UTF_8 );
        try
        {
            final Pre4NodeVersionJson nodeVersionJson = deserializeValue( charSource.read(), Pre4NodeVersionJson.class );
            return nodeVersionJson.fromJson();
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read node version [" + dumpBlobRecord.getKey() + "]", e );
        }
    }

    private void writeNodeVersion( final NodeVersion nodeVersion, final DumpBlobRecord dumpBlobRecord )
    {
        final String serializedUpgradedNodeVersion = serializeValue( nodeVersion );
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
