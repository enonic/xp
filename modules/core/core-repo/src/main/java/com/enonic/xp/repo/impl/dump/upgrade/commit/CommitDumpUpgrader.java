package com.enonic.xp.repo.impl.dump.upgrade.commit;

import java.nio.file.Path;
import java.util.HashSet;

import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.repo.impl.dump.serializer.json.BranchDumpEntryJson;
import com.enonic.xp.repo.impl.dump.serializer.json.CommitDumpEntryJson;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionsDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.AbstractMetaDumpUpgrader;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre6.Pre6BranchDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre6.Pre6VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre6.Pre6VersionsDumpEntryJson;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.Version;

public class CommitDumpUpgrader
    extends AbstractMetaDumpUpgrader
{
    private static final Version MODEL_VERSION = new Version( 6 );

    private static final String NAME = "Version commit";

    final HashSet<String> commitedNodeIds = new HashSet<>();

    final HashSet<String> commitedVersionIds = new HashSet<>();

    boolean processingMaster = false;

    private String commitId;

    public CommitDumpUpgrader( final Path basePath )
    {
        super( basePath );
    }

    @Override
    protected void upgradeRepository( final RepositoryId repositoryId )
    {
        commitedNodeIds.clear();
        commitedVersionIds.clear();

        processingMaster = true;
        createCommit( repositoryId );
        upgradeBranch( repositoryId, RepositoryConstants.MASTER_BRANCH );
        processingMaster = false;

        if ( !commitedNodeIds.isEmpty() )
        {
            dumpReader.getBranches( repositoryId ).
                stream().
                filter( branch -> !RepositoryConstants.MASTER_BRANCH.equals( branch ) ).
                forEach( branch -> upgradeBranch( repositoryId, branch ) );

            final Path versionsFile = dumpReader.getVersionsFile( repositoryId );
            if ( versionsFile != null )
            {
                upgradeVersionEntries( repositoryId, versionsFile );
            }
        }
    }

    private void createCommit( final RepositoryId repositoryId )
    {
        tmpDumpWriter.openCommitsMeta( repositoryId );
        try
        {
            final NodeCommitEntry nodeCommitEntry = NodeCommitEntry.create().build();
            commitId = new NodeCommitId().toString();
            final CommitDumpEntryJson commitDumpEntryJson = CommitDumpEntryJson.create().
                commitId( commitId ).
                message( "Dump upgrade" ).
                committer( nodeCommitEntry.getCommitter().toString() ).
                timestamp( nodeCommitEntry.getTimestamp().toString() ).
                build();
            final String commitDumpEntrySerialized = serialize( commitDumpEntryJson );
            tmpDumpWriter.storeTarEntry( commitDumpEntrySerialized, commitId + ".json" );
        }
        finally
        {
            tmpDumpWriter.close();
        }
    }

    @Override
    protected String upgradeVersionEntry( final RepositoryId repositoryId, final String entryContent )
    {
        final Pre6VersionsDumpEntryJson sourceEntry = deserializeValue( entryContent, Pre6VersionsDumpEntryJson.class );

        final VersionsDumpEntryJson.Builder upgradedEntry = VersionsDumpEntryJson.create().nodeId( sourceEntry.getNodeId() );

        sourceEntry.getVersions().
            stream().
            map( version -> {
                final boolean commit = commitedVersionIds.contains( version.getVersion() );
                return upgradeVersionDumpEntry( version, commit );
            } ).
            forEach( upgradedEntry::version );

        return serialize( upgradedEntry.build() );
    }

    @Override
    protected String upgradeBranchEntry( final RepositoryId repositoryId, final String entryContent )
    {
        if ( processingMaster )
        {
            final Pre6BranchDumpEntryJson sourceEntry = deserializeValue( entryContent, Pre6BranchDumpEntryJson.class );
            commitedNodeIds.add( sourceEntry.getNodeId() );
            commitedVersionIds.add( sourceEntry.getMeta().getVersion() );

            final BranchDumpEntryJson upgradedEntry = upgradeBranchDumpEntry( sourceEntry, true );
            return serialize( upgradedEntry );
        }
        else
        {
            final Pre6BranchDumpEntryJson sourceEntry = deserializeValue( entryContent, Pre6BranchDumpEntryJson.class );
            final boolean commit = commitedVersionIds.contains( sourceEntry.getMeta().getVersion() );
            final BranchDumpEntryJson upgradedEntry = upgradeBranchDumpEntry( sourceEntry, commit );
            return serialize( upgradedEntry );
        }
    }

    private BranchDumpEntryJson upgradeBranchDumpEntry( final Pre6BranchDumpEntryJson sourceEntry, final boolean commit )
    {
        final VersionDumpEntryJson upgradedMeta = upgradeVersionDumpEntry( sourceEntry.getMeta(), commit );

        return BranchDumpEntryJson.create().
            nodeId( sourceEntry.getNodeId() ).
            meta( upgradedMeta ).
            build();
    }

    private VersionDumpEntryJson upgradeVersionDumpEntry( final Pre6VersionDumpEntryJson sourceEntry, final boolean commit )
    {
        return VersionDumpEntryJson.create().
            nodePath( sourceEntry.getNodePath() ).
            timestamp( sourceEntry.getTimestamp() ).
            version( sourceEntry.getVersion() ).
            nodeBlobKey( sourceEntry.getNodeBlobKey() ).
            indexConfigBlobKey( sourceEntry.getIndexConfigBlobKey() ).
            accessControlBlobKey( sourceEntry.getAccessControlBlobKey() ).
            nodeState( sourceEntry.getNodeState() ).
            commitId( commit ? commitId : null ).
            build();
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
}
