package com.enonic.wem.api.vfs;

import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class VirtualFilesTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testName()
        throws Exception
    {

        final VirtualFile folder = VirtualFiles.from( Paths.get( temporaryFolder.getRoot().toPath().toString() ) );

        assertTrue( folder.isFolder() );
        assertTrue( folder.exists() );


    }
}