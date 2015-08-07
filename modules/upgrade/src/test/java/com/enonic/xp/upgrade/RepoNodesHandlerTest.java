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

import static junit.framework.TestCase.assertTrue;

public class RepoNodesHandlerTest
{
    @Rule
    public TemporaryFolder dumpRoot = new TemporaryFolder();

    @Rule
    public TemporaryFolder targetRoot = new TemporaryFolder();

    @Test
    public void untouched_files_copied()
        throws Exception
    {
        createDump();

        RepoNodesHandler.create().
            sourceRoot( dumpRoot.getRoot().toPath() ).
            target( targetRoot.getRoot().toPath() ).
            upgradeModels( Lists.newArrayList() ).
            build().
            execute();

        assertTarget();
    }

    @Test
    public void touched_files_written()
        throws Exception
    {
        createDump();

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
            sourceRoot( dumpRoot.getRoot().toPath() ).
            target( targetRoot.getRoot().toPath() ).
            upgradeModels( Lists.newArrayList( model ) ).
            build().
            execute();

        assertTarget();
    }

    private void createDump()
        throws Exception
    {
        final String root = dumpRoot.getRoot().toString();

        doCreateFile( root, "cms-repo", "draft", "_", "node.xml" );
        doCreateFile( root, "cms-repo", "master", "_", "node.xml" );
        doCreateFile( root, "system-repo", "master", "_", "node.xml" );
    }

    private boolean doCreateFile( final String first, final String... elements )
        throws Exception
    {
        final File file = Paths.get( first, elements ).toFile();
        return file.getParentFile().mkdirs() && file.createNewFile();
    }

    private void assertTarget()
    {
        assertTrue( Files.exists( Paths.get( targetRoot.getRoot().getPath(), "cms-repo", "draft", "_", "node.xml" ) ) );
        assertTrue( Files.exists( Paths.get( targetRoot.getRoot().getPath(), "cms-repo", "master", "_", "node.xml" ) ) );
        assertTrue( Files.exists( Paths.get( targetRoot.getRoot().getPath(), "system-repo", "master", "_", "node.xml" ) ) );
    }


}