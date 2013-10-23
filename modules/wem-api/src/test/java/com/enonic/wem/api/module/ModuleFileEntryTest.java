package com.enonic.wem.api.module;

import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.Test;

import com.google.common.io.ByteSource;

import static com.enonic.wem.api.module.ModuleFileEntry.directoryBuilder;
import static com.enonic.wem.api.module.ModuleFileEntry.newFileEntry;
import static com.google.common.io.ByteStreams.asByteSource;
import static org.junit.Assert.*;

public class ModuleFileEntryTest
{
    @Test
    public void testCreateModuleFileEntry()
    {
        final ByteSource byteSource = asByteSource( "data".getBytes() );
        final ModuleFileEntry entry = newFileEntry( "resource-name", byteSource );

        // verify
        assertEquals( "resource-name", entry.getName() );
        assertEquals( byteSource, entry.getResource().getByteSource() );
        assertFalse( entry.isDirectory() );
        assertTrue( entry.isFile() );
        assertTrue( entry.isEmpty() );
    }

    @Test
    public void testCreateModuleFileEntryFromFile()
    {
        final ModuleFileEntry entry = newFileEntry( Paths.get( "/temp/file.ext" ) );

        // verify
        assertEquals( "file.ext", entry.getName() );
        assertNotNull( entry.getResource().getByteSource() );
        assertFalse( entry.isDirectory() );
        assertTrue( entry.isFile() );
        assertTrue( entry.isEmpty() );
    }

    @Test
    public void testCreateModuleFileEntryDirectoryEmpty()
    {
        final ModuleFileEntry directory = directoryBuilder( "public" ).build();

        // verify
        assertEquals( "public", directory.getName() );
        assertEquals( null, directory.getResource() );
        assertTrue( directory.isDirectory() );
        assertFalse( directory.isFile() );
        assertTrue( directory.isEmpty() );
        assertFalse( directory.iterator().hasNext() );
    }

    @Test
    public void testCreateModuleFileEntryDirectoryWithFiles()
    {
        final ModuleFileEntry directory = directoryBuilder( "public" ).
            addFile( Paths.get( "/temp/text1.txt" ) ).
            addFile( Paths.get( "/temp/text2.txt" ) ).
            build();

        // verify
        assertEquals( "public", directory.getName() );
        assertEquals( null, directory.getResource() );
        assertTrue( directory.isDirectory() );
        assertFalse( directory.isFile() );
        assertFalse( directory.isEmpty() );
        assertTrue( directory.iterator().hasNext() );
        final Iterator<ModuleFileEntry> iterator = directory.iterator();
        assertEquals( "text1.txt", iterator.next().getName() );
        assertEquals( "text2.txt", iterator.next().getName() );
    }

    @Test
    public void testCreateModuleFileEntryDirectoryWith2Levels()
    {
        final ModuleFileEntry.Builder directoryBuilder = directoryBuilder( "public" ).
            addFile( Paths.get( "/temp/text1.txt" ) ).
            addFile( Paths.get( "/temp/text2.txt" ) ).
            addFile( "text3.txt", asByteSource( "data".getBytes() ) );

        final ModuleFileEntry.Builder subDirectory = directoryBuilder( "javascript" ).
            addFile( Paths.get( "/temp/file.js" ) ).
            addFile( Paths.get( "/temp/file2.js" ) );

        final ModuleFileEntry directory = directoryBuilder.addEntry( subDirectory ).build();

        // verify
        assertEquals( "public", directory.getName() );
        assertTrue( directory.isDirectory() );

        assertEquals( "javascript", directory.getEntry( "javascript" ).getName() );
        assertTrue( directory.getEntry( "javascript" ).isDirectory() );
        assertEquals( 2, directory.getEntry( "javascript" ).size() );
        assertEquals( 2, directory.getEntry( "javascript" ).entries().size() );
    }

    @Test
    public void testGetEntryByPath()
    {
        final ModuleFileEntry.Builder directoryBuilder = directoryBuilder( "public" ).
            addFile( Paths.get( "/temp/text1.txt" ) ).
            addFile( Paths.get( "/temp/text2.txt" ) ).
            addFile( Paths.get( "/temp/text3.txt" ) );

        final ModuleFileEntry.Builder subDirectory = directoryBuilder( "javascript" ).
            addFile( Paths.get( "/temp/file.js" ) ).
            addFile( Paths.get( "/temp/file2.js" ) );

        final ModuleFileEntry directory = directoryBuilder.addEntry( subDirectory ).build();

        // verify
        assertEquals( "javascript", directory.getEntry( "/javascript" ).getName() );
        assertEquals( "javascript", directory.getEntry( "javascript" ).getName() );
        assertEquals( "file.js", directory.getEntry( "javascript/file.js" ).getName() );
        assertEquals( "file2.js", directory.getEntry( "javascript/file2.js" ).getName() );
        assertNotNull( directory.getResource( "javascript/file2.js" ) );
        assertEquals( "file.js", directory.getEntry( "javascript" ).getEntry( "file.js" ).getName() );
        assertNull( directory.getEntry( "something" ) );
        assertNull( directory.getEntry( "something/more" ) );
        assertNull( directory.getEntry( "javascript/notfound.js" ) );
        assertNull( directory.getEntry( "public" ) );
    }

    @Test
    public void testEditByCopy()
    {
        final ModuleFileEntry.Builder directoryBuilder = directoryBuilder( "public" ).
            addFile( Paths.get( "/temp/text1.txt" ) ).
            addFile( Paths.get( "/temp/text2.txt" ) ).
            addFile( Paths.get( "/temp/text3.txt" ) );
        final ModuleFileEntry.Builder subDirectory = directoryBuilder( "javascript" ).
            addFile( Paths.get( "/temp/file.js" ) ).
            addFile( Paths.get( "/temp/file2.js" ) );
        final ModuleFileEntry directory = directoryBuilder.addEntry( subDirectory ).build();
        System.out.println( directory.asTreeString() + "\r\n----\r\n" );

        final ModuleFileEntry.Builder editedDir = ModuleFileEntry.copyOf( directory ).
            name( "private" ).
            remove( "text1.txt" );
        editedDir.getEntry( "text3.txt" ).name( "text3.txt.backup" );
        editedDir.getEntry( "javascript" ).addEntry( directoryBuilder( "emptyDir" ) );

        System.out.println( editedDir.build().asTreeString() );
    }
}
