package com.enonic.xp.core.impl.app.resolver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.enonic.xp.resource.ResourceKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileResourceTest
{
    private static final ResourceKey KEY = ResourceKey.from( "myapp:/cms/cms.yaml" );

    @TempDir
    Path dir;

    @Test
    void existing_file()
        throws Exception
    {
        final Path file = Files.writeString( dir.resolve( "cms.yaml" ), "kind: \"CMS\"" );
        final FileResource resource = new FileResource( KEY, file );

        assertTrue( resource.exists() );
        assertEquals( KEY, resource.getKey() );
        assertEquals( "kind: \"CMS\"", resource.readString() );
        assertEquals( Files.size( file ), resource.getSize() );
        assertEquals( "file", resource.getResolverName() );
        assertEquals( Files.getLastModifiedTime( file ).toMillis(), resource.getTimestamp() );
    }

    @Test
    void missing_file()
    {
        final FileResource resource = new FileResource( KEY, dir.resolve( "missing.yaml" ) );

        assertFalse( resource.exists() );
        assertEquals( -1, resource.getSize() );
        assertEquals( -1, resource.getTimestamp() );
        assertNull( resource.revision() );
    }

    // the revision of a file is its modification time as the file system keeps it: the same while the file is untouched, new once it
    // is written, back to none once it is gone
    @Test
    void revision_follows_the_modification_time()
        throws Exception
    {
        final Path file = Files.writeString( dir.resolve( "cms.yaml" ), "kind: \"CMS\"" );
        final FileResource resource = new FileResource( KEY, file );

        final Object revision = resource.revision();
        assertEquals( Files.getLastModifiedTime( file ), revision );
        assertEquals( revision, new FileResource( KEY, file ).revision() );

        Files.setLastModifiedTime( file, FileTime.fromMillis( Files.getLastModifiedTime( file ).toMillis() + 10_000 ) );
        assertNotEquals( revision, resource.revision() );

        Files.delete( file );
        assertNull( resource.revision() );
    }
}
