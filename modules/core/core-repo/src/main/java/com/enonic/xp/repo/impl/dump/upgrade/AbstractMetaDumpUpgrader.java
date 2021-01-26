package com.enonic.xp.repo.impl.dump.upgrade;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.dump.DefaultFilePaths;
import com.enonic.xp.repo.impl.dump.FilePaths;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.reader.FileDumpReader;
import com.enonic.xp.repo.impl.dump.writer.FileDumpWriter;
import com.enonic.xp.repository.RepositoryId;

public abstract class AbstractMetaDumpUpgrader
    extends AbstractDumpUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( AbstractMetaDumpUpgrader.class );

    protected FileDumpReader tmpDumpReader;

    protected FileDumpWriter tmpDumpWriter;

    public AbstractMetaDumpUpgrader( final Path basePath )
    {
        super( basePath );
    }

    @Override
    public void doUpgrade( final String dumpName )
    {
        super.doUpgrade( dumpName );
        final String timeMillis = Long.toString( System.currentTimeMillis() );
        FilePaths tmpFilePaths = new DefaultFilePaths()
        {
            @Override
            public PathRef branchMetaPath( final RepositoryId repositoryId, final Branch branch )
            {
                return branchRootPath( repositoryId ).resolve( branch.toString() ).resolve( "meta-" + timeMillis + ".tar.gz" );
            }

            @Override
            public PathRef versionMetaPath( final RepositoryId repositoryId )
            {
                return branchRootPath( repositoryId ).resolve( "versions-" + timeMillis + ".tar.gz" );
            }
        };
        tmpDumpReader = FileDumpReader.create( null, basePath, dumpName, tmpFilePaths );
        tmpDumpWriter = FileDumpWriter.create( basePath, dumpName, null, tmpFilePaths );

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
            try
            {
                tmpDumpReader.close();
                tmpDumpWriter.close();
            }
            catch ( IOException e )
            {
                LOG.error( "Error while closing dump writer", e );
            }
        }
    }

    protected void upgradeRepository( final RepositoryId repositoryId )
    {
        final Path versionsFile = dumpReader.getVersionsFile( repositoryId );
        if ( Files.exists( versionsFile ) )
        {
            upgradeVersionEntries( repositoryId, versionsFile );
        }
        dumpReader.getBranches( repositoryId ).
            forEach( branch -> upgradeBranch( repositoryId, branch ) );
    }

    protected void upgradeBranch( final RepositoryId repositoryId, final Branch branch )
    {
        final Path entriesFile = dumpReader.getBranchEntriesFile( repositoryId, branch );
        if ( Files.exists( entriesFile ) )
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
            processEntries( ( entryContent, entryName ) -> {
                byte[] upgradedEntryContent = null;
                String upgradedEntryName = entryName;
                if ( hasToUpgradeEntry( repositoryId, entryName ) )
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
                tmpDumpWriter.storeTarEntry(
                    upgradedEntryContent == null ? entryContent.getBytes( StandardCharsets.UTF_8 ) : upgradedEntryContent,
                    upgradedEntryName );
            }, entriesFile );
        }
        finally
        {
            tmpDumpWriter.closeMeta();
        }
    }

    protected void upgradeBranchEntries( final RepositoryId repositoryId, final Branch branch, final Path entriesFile )
    {
        tmpDumpWriter.openBranchMeta( repositoryId, branch );
        try
        {
            processEntries( ( entryContent, entryName ) -> {

                byte[] upgradedEntryContent = null;
                String upgradedEntryName = entryName;
                if ( hasToUpgradeEntry( repositoryId, entryName ) )
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
                tmpDumpWriter.storeTarEntry(
                    upgradedEntryContent == null ? entryContent.getBytes( StandardCharsets.UTF_8 ) : upgradedEntryContent,
                    upgradedEntryName );
            }, entriesFile );
        }
        finally
        {
            tmpDumpWriter.closeMeta();
        }
    }

    private void overwriteSourceFiles()
        throws IOException
    {
        for ( RepositoryId repositoryId : dumpReader.getRepositories() )
        {
            final Path newVersions = tmpDumpReader.getVersionsFile( repositoryId );

            if ( Files.exists( newVersions ) )
            {
                Files.move( newVersions, dumpReader.getVersionsFile( repositoryId ), StandardCopyOption.REPLACE_EXISTING );
            }

            for ( Branch branch : dumpReader.getBranches( repositoryId ) )
            {
                final Path newBranch = tmpDumpReader.getBranchEntriesFile( repositoryId, branch );

                if ( Files.exists( newBranch ) )
                {
                    Files.move( newBranch, dumpReader.getBranchEntriesFile( repositoryId, branch ), StandardCopyOption.REPLACE_EXISTING );
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

    protected boolean hasToUpgradeEntry( final RepositoryId repositoryId, final String entryName )
    {
        return true;
    }

    protected String upgradeEntryName( final RepositoryId repositoryId, final String entryName )
    {
        return entryName;
    }

    protected abstract byte[] upgradeVersionEntry( RepositoryId repositoryId, String entryContent );

    protected abstract byte[] upgradeBranchEntry( RepositoryId repositoryId, String entryContent );
}
