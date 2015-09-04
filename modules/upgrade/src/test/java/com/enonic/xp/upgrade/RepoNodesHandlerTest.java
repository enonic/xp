package com.enonic.xp.upgrade;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Lists;
import com.google.common.io.CharSource;

import com.enonic.xp.upgrade.model.UpgradeModel;

import static org.junit.Assert.assertTrue;

public class RepoNodesHandlerTest
{
    @Rule
    public TemporaryFolder dumpFolder = new TemporaryFolder();

    @Test
    public void untouched_files_copied()
        throws Exception
    {
        final Path dumpRoot = this.dumpFolder.newFolder( "testDump" ).toPath();

        createDump( dumpRoot );

        RepoNodesHandler.create().
            sourceRoot( dumpRoot ).
            upgradeModels( Lists.newArrayList() ).
            build().
            execute();

        assertTarget( UpgradePathHelper.generateUpgradeTargetPath( dumpFolder.getRoot().toPath(), "testDump" ) );
    }

    @Test
    public void touched_files_written()
        throws Exception
    {

        final Path dumpRoot = this.dumpFolder.newFolder( "testDump" ).toPath();

        createDump( dumpRoot );

        UpgradeModel model = new UpgradeModel()
        {
            @Override
            public boolean supports( final Path path, final String repositoryName, final String branchName )
            {
                return true;
            }

            @Override
            public String upgrade( final Path path, final CharSource source )
            {
                try
                {
                    return source.read();
                }
                catch ( IOException e )
                {
                    throw new RuntimeException();
                }
            }

            @Override
            public void log()
            {
                // Do nothing
            }
        };

        RepoNodesHandler.create().
            sourceRoot( dumpRoot ).
            upgradeModels( Lists.newArrayList( model ) ).
            build().
            execute();

        assertTarget( UpgradePathHelper.generateUpgradeTargetPath( dumpFolder.getRoot().toPath(), "testDump" ) );
    }

    private void createDump( final Path dumpFolder )
        throws Exception
    {
        doCreateFile( dumpFolder.toString(), "cms-repo", "draft", "_", "node.xml" );
        doCreateFile( dumpFolder.toString(), "cms-repo", "master", "_", "node.xml" );
        doCreateFile( dumpFolder.toString(), "system-repo", "master", "_", "node.xml" );
    }

    private boolean doCreateFile( final String first, final String... elements )
        throws Exception
    {
        final File file = Paths.get( first, elements ).toFile();
        return file.getParentFile().mkdirs() && file.createNewFile();
    }

    private void assertTarget( final Path targetRoot )
    {
        assertTrue( Files.exists( Paths.get( targetRoot.toString(), "cms-repo", "draft", "_", "node.xml" ) ) );
        assertTrue( Files.exists( Paths.get( targetRoot.toString(), "cms-repo", "master", "_", "node.xml" ) ) );
        assertTrue( Files.exists( Paths.get( targetRoot.toString(), "system-repo", "master", "_", "node.xml" ) ) );
    }


}