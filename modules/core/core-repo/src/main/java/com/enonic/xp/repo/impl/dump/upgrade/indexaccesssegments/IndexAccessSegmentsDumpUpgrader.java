package com.enonic.xp.repo.impl.dump.upgrade.indexaccesssegments;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.repo.impl.dump.DumpBlobRecord;
import com.enonic.xp.repo.impl.dump.DumpConstants;
import com.enonic.xp.repo.impl.dump.upgrade.AbstractMetaDumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.DumpUpgradeException;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4.Pre4BranchDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4.Pre4NodeVersionJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4.Pre4VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4.Pre4VersionsDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre6.Pre6BranchDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre6.Pre6VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre6.Pre6VersionsDumpEntryJson;
import com.enonic.xp.repo.impl.node.json.AccessControlJson;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.util.Version;

public class IndexAccessSegmentsDumpUpgrader
    extends AbstractMetaDumpUpgrader
{
    private static final Version MODEL_VERSION = new Version( 4, 0, 0 );

    public IndexAccessSegmentsDumpUpgrader( final Path basePath )
    {
        super( basePath );
    }

    @Override
    public Version getModelVersion()
    {
        return MODEL_VERSION;
    }

    @Override
    protected String upgradeVersionEntry( final RepositoryId repositoryId, final String entryContent )
    {
        final Pre4VersionsDumpEntryJson versionsDumpEntry = deserializeValue( entryContent, Pre4VersionsDumpEntryJson.class );

        Collection<Pre6VersionDumpEntryJson> versions = versionsDumpEntry.getVersions().
            stream().
            map( versionDumpEntry -> upgradeVersionDumpEntry( repositoryId, versionDumpEntry ) ).
            collect( Collectors.toList() );

        final Pre6VersionsDumpEntryJson upgradedVersionsDumpEntry = Pre6VersionsDumpEntryJson.create().
            nodeId( versionsDumpEntry.getNodeId() ).
            versions( versions ).
            build();

        return serialize( upgradedVersionsDumpEntry );
    }

    @Override
    protected String upgradeBranchEntry( final RepositoryId repositoryId, final String entryContent )
    {
        final Pre4BranchDumpEntryJson branchDumpEntry = deserializeValue( entryContent, Pre4BranchDumpEntryJson.class );
        final Pre6VersionDumpEntryJson upgradedVersionDumpEntry = upgradeVersionDumpEntry( repositoryId, branchDumpEntry.getMeta() );

        final Pre6BranchDumpEntryJson upgradedBranchDumpEntry = Pre6BranchDumpEntryJson.create().
            nodeId( branchDumpEntry.getNodeId() ).
            binaries( branchDumpEntry.getBinaries() ).
            meta( upgradedVersionDumpEntry ).
            build();

        return serialize( upgradedBranchDumpEntry );
    }

    private Pre6VersionDumpEntryJson upgradeVersionDumpEntry( final RepositoryId repositoryId,
                                                              final Pre4VersionDumpEntryJson versionDumpEntry )
    {
        //Retrieves the existing node version
        final Segment nodeDataSegment = RepositorySegmentUtils.toSegment( repositoryId, DumpConstants.DUMP_NODE_SEGMENT_LEVEL );
        final BlobKey nodeBlobKey = BlobKey.from( versionDumpEntry.getBlobKey() );
        final BlobRecord nodeBlobRecord = dumpReader.getDumpBlobStore().
            getRecord( nodeDataSegment, nodeBlobKey );
        final Pre4NodeVersionJson nodeVersion = getNodeVersion( (DumpBlobRecord) nodeBlobRecord );

        //Serializes the new index config blob
        final String serializedIndexConfig = serialize( nodeVersion.getIndexConfigDocument() );
        final Segment indexConfigSegment = RepositorySegmentUtils.toSegment( repositoryId, DumpConstants.DUMP_INDEX_CONFIG_SEGMENT_LEVEL );
        final BlobKey indexConfigBlobKey = addRecord( indexConfigSegment, serializedIndexConfig );

        //Serializes the new access control blob
        final String serializedAccessControl = serializeAccessControl( nodeVersion );
        final Segment accessControlSegment =
            RepositorySegmentUtils.toSegment( repositoryId, DumpConstants.DUMP_ACCESS_CONTROL_SEGMENT_LEVEL );
        final BlobKey accessControlBlobKey = addRecord( accessControlSegment, serializedAccessControl );

        //Return the new version dump entry
        return Pre6VersionDumpEntryJson.create().
            nodePath( versionDumpEntry.getNodePath() ).
            timestamp( versionDumpEntry.getTimestamp() ).
            version( versionDumpEntry.getVersion() ).
            nodeBlobKey( versionDumpEntry.getBlobKey() ).
            indexConfigBlobKey( indexConfigBlobKey.toString() ).
            accessControlBlobKey( accessControlBlobKey.toString() ).
            nodeState( versionDumpEntry.getNodeState() ).
            build();
    }

    private Pre4NodeVersionJson getNodeVersion( final DumpBlobRecord dumpBlobRecord )
    {
        final CharSource charSource = dumpBlobRecord.getBytes().asCharSource( Charsets.UTF_8 );
        try
        {
            return deserializeValue( charSource.read(), Pre4NodeVersionJson.class );
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read node version [" + dumpBlobRecord.getKey() + "]", e );
        }
    }

    private String serializeAccessControl( final Pre4NodeVersionJson nodeVersion )
    {
        final AccessControlJson accessControlJson = AccessControlJson.create().
            inheritPermissions( nodeVersion.isInheritPermissions() ).
            permissions( nodeVersion.getPermissions() ).
            build();
        return serialize( accessControlJson );
    }
}
