package com.enonic.xp.repo.impl.dump.upgrade;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public abstract class AbstractMetaDumpUpgrader
    extends AbstractDumpUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( AbstractMetaDumpUpgrader.class );

    protected BufferFileDumpReader tmpDumpReader;

    protected BufferFileDumpWriter tmpDumpWriter;

    public AbstractMetaDumpUpgrader( final Path basePath )
    {
        super( basePath );
    }

    @Override
    public void doUpgrade( final String dumpName )
    {
        super.doUpgrade( dumpName );
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
        final Path versionsFile = dumpReader.getVersionsFile( repositoryId );
        if ( versionsFile != null )
        {
            upgradeVersionEntries( repositoryId, versionsFile );
        }
        dumpReader.getBranches( repositoryId ).
            forEach( branch -> upgradeBranch( repositoryId, branch ) );
    }

    protected void upgradeBranch( final RepositoryId repositoryId, final Branch branch )
    {
        final Path entriesFile = dumpReader.getBranchEntriesFile( repositoryId, branch );
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

    protected void upgradeVersionEntries( final RepositoryId repositoryId, final Path entriesFile )
    {
        tmpDumpWriter.openVersionsMeta( repositoryId );
        try
        {
            dumpReader.processEntries( ( entryContent, entryName ) -> {
                String upgradedEntryContent = entryContent;
                String upgradedEntryName = entryName;
                if ( hasToUpgradeEntry( repositoryId, entryContent, entryName ) )
                {
                    result.processed();
                    try
                    {
                        upgradedEntryContent = upgradeVersionEntry( repositoryId, entryContent );
                        upgradedEntryName = upgradeEntryName( repositoryId, entryName );
                    }
                    catch ( Exception e )
                    {
                        result.error();
                        LOG.error( "Error while upgrading version entry [" + entryName + "]", e );
                    }
                }
                tmpDumpWriter.storeTarEntry( upgradedEntryContent, upgradedEntryName );
            }, entriesFile );
        }
        finally
        {
            tmpDumpWriter.close();
        }
    }

    protected void upgradeBranchEntries( final RepositoryId repositoryId, final Branch branch, final Path entriesFile )
    {
        tmpDumpWriter.openBranchMeta( repositoryId, branch );
        try
        {
            dumpReader.processEntries( ( entryContent, entryName ) -> {

                String upgradedEntryContent = entryContent;
                String upgradedEntryName = entryName;
                if ( hasToUpgradeEntry( repositoryId, entryContent, entryName ) )
                {
                    result.processed();
                    try
                    {
                        upgradedEntryContent = upgradeBranchEntry( repositoryId, entryContent );
                        upgradedEntryName = upgradeEntryName( repositoryId, entryName );
                    }
                    catch ( Exception e )
                    {
                        result.error();
                        LOG.error( "Error while upgrading branch entry [" + entryName + "]", e );
                    }
                }
                tmpDumpWriter.storeTarEntry( upgradedEntryContent, upgradedEntryName );
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
            final Path newVersions = tmpDumpReader.getVersionsFile( repositoryId );

            if ( newVersions != null )
            {
                Files.move( tmpDumpReader.getVersionsFile( repositoryId ), dumpReader.getVersionsFile( repositoryId ),
                            StandardCopyOption.REPLACE_EXISTING );
            }

            for ( Branch branch : dumpReader.getBranches( repositoryId ) )
            {
                final Path newBranch = tmpDumpReader.getBranchEntriesFile( repositoryId, branch );

                if ( newBranch != null )
                {
                    Files.move( tmpDumpReader.getBranchEntriesFile( repositoryId, branch ),
                                dumpReader.getBranchEntriesFile( repositoryId, branch ), StandardCopyOption.REPLACE_EXISTING );
                }

            }
        }
    }

    private void deleteBufferFiles()
        throws IOException
    {
        Path bufferFile;
        for ( RepositoryId repositoryId : dumpReader.getRepositories() )
        {
            bufferFile = tmpDumpReader.getVersionsFile( repositoryId );
            Files.deleteIfExists( bufferFile );

            for ( Branch branch : dumpReader.getBranches( repositoryId ) )
            {
                bufferFile = tmpDumpReader.getBranchEntriesFile( repositoryId, branch );
                Files.deleteIfExists( bufferFile );
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
