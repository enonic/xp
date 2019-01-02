package com.enonic.xp.repo.impl.dump.upgrade;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.io.Files;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.reader.FileDumpReader;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre2.Pre2BranchDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre2.Pre2VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre2.Pre2VersionsDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4.Pre4BranchDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4.Pre4VersionDumpEntryJson;
import com.enonic.xp.repo.impl.dump.upgrade.obsoletemodel.pre4.Pre4VersionsDumpEntryJson;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.Version;

public class VersionIdDumpUpgrader
    extends AbstractDumpUpgrader
{
    private final Logger LOG = LoggerFactory.getLogger( VersionIdDumpUpgrader.class );

    private final Path basePath;

    private FileDumpReader dumpReader;

    private BufferFileDumpReader tmpDumpReader;

    private BufferFileDumpWriter tmpDumpWriter;

    public VersionIdDumpUpgrader( final Path basePath )
    {
        this.basePath = basePath;
    }

    @Override
    public Version getModelVersion()
    {
        return new Version( 2, 0, 0 );
    }

    @Override
    public void upgrade( final String dumpName )
    {

        this.dumpReader = new FileDumpReader( basePath, dumpName, null );
        final String timeMillis = Long.toString( System.currentTimeMillis() );
        this.tmpDumpReader = new BufferFileDumpReader( basePath, dumpName, null, timeMillis );
        this.tmpDumpWriter = new BufferFileDumpWriter( basePath, dumpName, null, timeMillis );

        try
        {
            dumpReader.getRepositories().
                forEach( this::upgradeRepository );
            overwriteSourceFiles();
        }
        catch ( Exception e )
        {
            try
            {
                deleteBufferFiles();
            }
            catch ( Exception e2 )
            {
                LOG.error( "Error while deleting buffer files", e );
            }
            throw new DumpUpgradeException( "Error while upgrading dump [" + dumpName + "]", e );
        }
    }

    private void upgradeRepository( final RepositoryId repositoryId )
    {
        final File versionsFile = dumpReader.getVersionsFile( repositoryId );
        if ( versionsFile != null )
        {
            upgradeVersionEntries( repositoryId, versionsFile );
        }
        dumpReader.getBranches( repositoryId ).
            forEach( branch -> upgradeBranch( repositoryId, branch ) );
    }

    private void upgradeBranch( final RepositoryId repositoryId, final Branch branch )
    {
        final File entriesFile = dumpReader.getBranchEntriesFile( repositoryId, branch );
        if ( entriesFile != null )
        {
            upgradeBranchEntries( repositoryId, branch, entriesFile );
        }
        else
        {
            throw new DumpUpgradeException(
                "Branch entries file missing for repository [" + repositoryId + "] and branch [" + branch + "]" );
        }
    }

    private void upgradeVersionEntries( final RepositoryId repositoryId, final File entriesFile )
    {
        try
        {
            tmpDumpWriter.openVersionsMeta( repositoryId );

            dumpReader.processEntries( ( entryContent, entryName ) -> {
                final Pre2VersionsDumpEntryJson sourceEntry = deserializeValue( entryContent, Pre2VersionsDumpEntryJson.class );
                final Pre4VersionsDumpEntryJson.Builder upgradedEntry = Pre4VersionsDumpEntryJson.create().
                    nodeId( sourceEntry.getNodeId() );

                sourceEntry.getVersions().
                    stream().
                    map( this::upgradeVersion ).
                    forEach( upgradedEntry::version );

                final String upgradedEntryContent = serialize( upgradedEntry.build() );
                tmpDumpWriter.storeTarEntry( upgradedEntryContent, entryName );
            }, entriesFile );
        }
        finally
        {
            tmpDumpWriter.close();
        }
    }

    private void upgradeBranchEntries( final RepositoryId repositoryId, final Branch branch, final File entriesFile )
    {
        try
        {
            tmpDumpWriter.openBranchMeta( repositoryId, branch );

            dumpReader.processEntries( ( entryContent, entryName ) -> {
                final Pre2BranchDumpEntryJson sourceEntry = deserializeValue( entryContent, Pre2BranchDumpEntryJson.class );

                final Pre4BranchDumpEntryJson upgradedEntry = Pre4BranchDumpEntryJson.create().
                    nodeId( sourceEntry.getNodeId() ).
                    meta( upgradeVersion( sourceEntry.getMeta() ) ).
                    binaries( sourceEntry.getBinaries() ).
                    build();

                final String upgradedEntryContent = serialize( upgradedEntry );
                tmpDumpWriter.storeTarEntry( upgradedEntryContent, entryName );
            }, entriesFile );
        }
        finally
        {
            tmpDumpWriter.close();
        }
    }

    private void overwriteSourceFiles()
        throws IOException
    {
        for ( RepositoryId repositoryId : dumpReader.getRepositories() )
        {
            Files.move( tmpDumpReader.getVersionsFile( repositoryId ), dumpReader.getVersionsFile( repositoryId ) );

            for ( Branch branch : dumpReader.getBranches( repositoryId ) )
            {
                Files.move( tmpDumpReader.getBranchEntriesFile( repositoryId, branch ),
                            dumpReader.getBranchEntriesFile( repositoryId, branch ) );
            }
        }
    }

    private Pre4VersionDumpEntryJson upgradeVersion( final Pre2VersionDumpEntryJson version )
    {
        return Pre4VersionDumpEntryJson.create().
            version( version.getVersion() ).
            blobKey( version.getVersion() ).
            nodePath( version.getNodePath() ).
            nodeState( version.getNodeState() ).
            timestamp( version.getTimestamp() ).
            build();
    }

    private void deleteBufferFiles()
    {
        File bufferFile;
        for ( RepositoryId repositoryId : dumpReader.getRepositories() )
        {
            bufferFile = tmpDumpReader.getVersionsFile( repositoryId );
            if ( bufferFile.exists() )
            {
                bufferFile.delete();
            }

            for ( Branch branch : dumpReader.getBranches( repositoryId ) )
            {
                bufferFile = tmpDumpReader.getBranchEntriesFile( repositoryId, branch );
                if ( bufferFile.exists() )
                {
                    bufferFile.delete();
                }
            }
        }
    }

    public String serialize( final Pre4VersionsDumpEntryJson versionsDumpEntry )
    {
        try
        {
            return mapper.writeValueAsString( versionsDumpEntry );
        }
        catch ( JsonProcessingException e )
        {
            throw new RepoDumpException( "Cannot serialize dumpEntry", e );
        }
    }

    public String serialize( final Pre4BranchDumpEntryJson branchDumpEntry )
    {
        try
        {
            return mapper.writeValueAsString( branchDumpEntry );
        }
        catch ( JsonProcessingException e )
        {
            throw new RepoDumpException( "Cannot serialize dumpEntry", e );
        }
    }
}
