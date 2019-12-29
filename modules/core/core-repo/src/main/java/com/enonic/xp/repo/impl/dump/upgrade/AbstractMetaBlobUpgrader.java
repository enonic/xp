package com.enonic.xp.repo.impl.dump.upgrade;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public abstract class AbstractMetaBlobUpgrader
    extends AbstractDumpUpgrader
{
    private static final Logger LOG = LoggerFactory.getLogger( AbstractMetaBlobUpgrader.class );

    public AbstractMetaBlobUpgrader( final Path basePath )
    {
        super( basePath );
    }

    @Override
    public void doUpgrade( final String dumpName )
    {
        super.doUpgrade( dumpName );

        try
        {
            dumpReader.getRepositories().
                forEach( this::upgradeRepository );
        }
        catch ( Exception e )
        {
            throw new DumpUpgradeException( "Error while upgrading dump [" + dumpName + "]", e );
        }
    }

    protected void upgradeRepository( final RepositoryId repositoryId )
    {
        final Path versionsFile = dumpReader.getVersionsFile( repositoryId );
        if ( versionsFile != null )
        {
            upgradeVersionEntries( repositoryId, versionsFile );
        } else {
            dumpReader.getBranches( repositoryId ).
                forEach( branch -> upgradeBranch( repositoryId, branch ) );
        }
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
        dumpReader.processEntries( ( entryContent, entryName ) -> {
            result.processed();
            try
            {
                upgradeVersionEntry( repositoryId, entryContent );
            }
            catch ( Exception e )
            {
                result.error();
                LOG.error( "Error while upgrading version entry [" + entryName + "]", e );
            }
        }, entriesFile );
    }

    protected void upgradeBranchEntries( final RepositoryId repositoryId, final Branch branch, final Path entriesFile )
    {
        dumpReader.processEntries( ( entryContent, entryName ) -> {
            result.processed();
            try
            {
                upgradeBranchEntry( repositoryId, entryContent );
            }
            catch ( Exception e )
            {
                result.error();
                LOG.error( "Error while upgrading branch entry [" + entryName + "]", e );
            }

        }, entriesFile );
    }

    protected abstract void upgradeVersionEntry( final RepositoryId repositoryId, final String entryContent );

    protected abstract void upgradeBranchEntry( final RepositoryId repositoryId, final String entryContent );
}
