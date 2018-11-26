package com.enonic.xp.repo.impl.dump.upgrade;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;
import com.enonic.xp.repo.impl.dump.reader.FileDumpReader;
import com.enonic.xp.repo.impl.dump.serializer.json.JsonDumpSerializer;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.Version;

public class VersionIdDumpUpgrader
    implements DumpUpgrader
{
    private final Logger LOG = LoggerFactory.getLogger( VersionIdDumpUpgrader.class );

    private final Path basePath;

    private FileDumpReader dumpReader;

    private BufferFileDumpReader tmpDumpReader;

    private BufferFileDumpWriter tmpDumpWriter;

    private JsonDumpSerializer serializer;

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
        this.serializer = new JsonDumpSerializer();

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
            dumpReader.getBranches( repositoryId ).
                forEach( branch -> upgradeBranch( repositoryId, branch ) );
        }
        else
        {
            throw new DumpUpgradeException( "Versions file missing for repository [" + repositoryId + "]" );
        }
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
            throw new DumpUpgradeException( "Versions file missing for repository [" + repositoryId + "] and branch [" + branch + "]" );
        }
    }

    private void upgradeVersionEntries( final RepositoryId repositoryId, final File entriesFile )
    {
        try
        {
            tmpDumpWriter.openVersionsMeta( repositoryId );

            dumpReader.processEntries( ( entryContent, entryName ) -> {
                System.out.println( "contentBefore: " + entryContent );
                final VersionsDumpEntry sourceEntry = serializer.toNodeVersionsEntry( entryContent );

                final VersionsDumpEntry.Builder upgradedEntry = VersionsDumpEntry.create( sourceEntry.getNodeId() );

                sourceEntry.getVersions().
                    stream().
                    map( this::upgradeVersionMeta ).
                    forEach( upgradedEntry::addVersion );

                final String upgradedEntryContent = serializer.serialize( upgradedEntry.build() );
                System.out.println( "contentAfter: " + upgradedEntryContent );
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
                System.out.println( "contentBefore: " + entryContent );
                final BranchDumpEntry sourceEntry = serializer.toBranchMetaEntry( entryContent );

                final BranchDumpEntry upgradedEntry = BranchDumpEntry.create().nodeId( sourceEntry.getNodeId() ).
                    meta( upgradeVersionMeta( sourceEntry.getMeta() ) ).
                    setBinaryReferences( sourceEntry.getBinaryReferences() ).
                    build();

                final String upgradedEntryContent = serializer.serialize( upgradedEntry );
                System.out.println( "contentAfter: " + upgradedEntryContent );
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

    private VersionMeta upgradeVersionMeta( final VersionMeta versionMeta )
    {
        return VersionMeta.create().
            version( versionMeta.getVersion() ).
            blobKey( BlobKey.from( versionMeta.getVersion().toString() ) ).
            nodePath( versionMeta.getNodePath() ).
            nodeState( versionMeta.getNodeState() ).
            timestamp( versionMeta.getTimestamp() ).
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
}
