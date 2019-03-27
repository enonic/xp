package com.enonic.xp.repo.impl.dump.upgrade;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public abstract class AbstractMetaDumpUpgrader
    extends AbstractDumpUpgrader
{
    private final Logger LOG = LoggerFactory.getLogger( VersionIdDumpUpgrader.class );

    protected BufferFileDumpReader tmpDumpReader;

    protected BufferFileDumpWriter tmpDumpWriter;

    public AbstractMetaDumpUpgrader( final Path basePath )
    {
        super( basePath );
    }

    @Override
    public void upgrade( final String dumpName )
    {
        super.upgrade( dumpName );
        final String timeMillis = Long.toString( System.currentTimeMillis() );
        tmpDumpReader = new BufferFileDumpReader( basePath, dumpName, null, timeMillis );
        tmpDumpWriter = new BufferFileDumpWriter( basePath, dumpName, null, timeMillis );

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
        finally
        {
            tmpDumpWriter.close();
        }
    }

    protected void upgradeRepository( final RepositoryId repositoryId )
    {
        final File versionsFile = dumpReader.getVersionsFile( repositoryId );
        if ( versionsFile != null )
        {
            upgradeVersionEntries( repositoryId, versionsFile );
        }
        dumpReader.getBranches( repositoryId ).
            forEach( branch -> upgradeBranch( repositoryId, branch ) );
    }

    protected void upgradeBranch( final RepositoryId repositoryId, final Branch branch )
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

    protected void upgradeVersionEntries( final RepositoryId repositoryId, final File entriesFile )
    {
        tmpDumpWriter.openVersionsMeta( repositoryId );
        try
        {
            dumpReader.processEntries( ( entryContent, entryName ) -> {

                if ( hasToUpgradeEntry( repositoryId, entryContent, entryName ) )
                {
                    tmpDumpWriter.storeTarEntry( upgradeVersionEntry( repositoryId, entryContent ),
                                                 upgradeEntryName( repositoryId, entryName ) );
                }
                else
                {
                    tmpDumpWriter.storeTarEntry( entryContent, entryName );
                }

            }, entriesFile );
        }
        finally
        {
            tmpDumpWriter.close();
        }
    }

    protected void upgradeBranchEntries( final RepositoryId repositoryId, final Branch branch, final File entriesFile )
    {
        tmpDumpWriter.openBranchMeta( repositoryId, branch );
        try
        {
            dumpReader.processEntries( ( entryContent, entryName ) -> {

                if ( hasToUpgradeEntry( repositoryId, entryContent, entryName ) )
                {
                    tmpDumpWriter.storeTarEntry( upgradeBranchEntry( repositoryId, entryContent ),
                                                 upgradeEntryName( repositoryId, entryName ) );
                }
                else
                {
                    tmpDumpWriter.storeTarEntry( entryContent, entryName );
                }
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
            final File newVersions = tmpDumpReader.getVersionsFile( repositoryId );

            if ( newVersions != null )
            {
                Files.move( tmpDumpReader.getVersionsFile( repositoryId ), dumpReader.getVersionsFile( repositoryId ) );
            }

            for ( Branch branch : dumpReader.getBranches( repositoryId ) )
            {
                final File newBranch = tmpDumpReader.getBranchEntriesFile( repositoryId, branch );

                if ( newBranch != null )
                {
                    Files.move( tmpDumpReader.getBranchEntriesFile( repositoryId, branch ),
                                dumpReader.getBranchEntriesFile( repositoryId, branch ) );
                }

            }
        }
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

    protected boolean hasToUpgradeEntry( final RepositoryId repositoryId, final String entryContent, final String entryName )
    {
        return true;
    }

    protected String upgradeEntryName( final RepositoryId repositoryId, final String entryName )
    {
        return entryName;
    }

    protected abstract String upgradeVersionEntry( final RepositoryId repositoryId, final String entryContent );

    protected abstract String upgradeBranchEntry( final RepositoryId repositoryId, final String entryContent );
}
