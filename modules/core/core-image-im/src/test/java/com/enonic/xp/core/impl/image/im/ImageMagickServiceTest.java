package com.enonic.xp.core.impl.image.im;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageMagickServiceTest
{
    @TempDir
    Path storage;

    @Test
    void concurrentHandlesShareFilesAndSurviveDeactivation() throws Exception
    {
        final var reads = new AtomicInteger();
        final var service = service( "magick.exe\nx\tmagick.exe\n", reads );
        try (var executor = Executors.newVirtualThreadPerTaskExecutor())
        {
            final var firstFuture = executor.submit( service::acquire );
            final var secondFuture = executor.submit( service::acquire );
            final var first = firstFuture.get();
            final var second = secondFuture.get();
            assertEquals( first.executable(), second.executable() );
            assertEquals( 2, reads.get() );
            service.deactivate();
            assertThrows( IOException.class, service::acquire );
            first.close();
            first.close();
            assertTrue( Files.isRegularFile( second.executable() ) );
            second.close();
            assertFalse( Files.exists( second.executable() ) );
        }
    }

    @Test
    void rejectsTraversalAndCleansFailedInstallation() throws Exception
    {
        for ( String name : List.of( "../outside", "/outside", "C:/outside", "dir/../../outside", "dir\\outside" ) )
        {
            final var service = service( "magick.exe\nx\t" + name + "\n", new AtomicInteger() );
            assertThrows( IOException.class, service::acquire );
            service.deactivate();
            try (var paths = Files.list( storage )) { assertEquals( 0, paths.count() ); }
        }
    }

    @Test
    void recreatesInstallationAfterComponentRestart() throws Exception
    {
        final var first = service( "magick.exe\nx\tmagick.exe\n", new AtomicInteger() );
        final Path old;
        try (var handle = first.acquire()) { old = handle.executable(); }
        first.deactivate();
        final var next = service( "magick.exe\nx\tmagick.exe\n", new AtomicInteger() );
        try (var handle = next.acquire())
        {
            assertFalse( old.equals( handle.executable() ) );
            assertFalse( Files.exists( old ) );
            assertTrue( Files.isRegularFile( handle.executable() ) );
        }
        next.deactivate();
    }

    @Test
    void everyPlatformContainsOrdinaryExecutableResources() throws Exception
    {
        for ( String platform : List.of( "linux-x86_64", "linux-aarch64", "windows-x86_64", "windows-aarch64", "osx-aarch64" ) )
        {
            final String root = "/native/imagemagick/" + platform + "/";
            try (var index = ImageMagickService.class.getResourceAsStream( root + "files.txt" ))
            {
                assertNotNull( index, platform );
                final String[] lines = new String( index.readAllBytes(), StandardCharsets.UTF_8 ).split( "\n" );
                assertTrue( lines.length > 2, platform );
                try (var binary = ImageMagickService.class.getResourceAsStream( root + lines[0] ))
                {
                    assertNotNull( binary, platform );
                    assertTrue( binary.readNBytes( 4 ).length == 4 );
                }
                for ( int i = 1; i < lines.length; i++ )
                {
                    assertFalse( lines[i].endsWith( ".AppImage" ) || lines[i].endsWith( ".zip" ), lines[i] );
                    assertFalse( lines[i].endsWith( "self-updater.hook" ), lines[i] );
                }
            }
        }
    }

    @Test
    void recognizesPlatformAliases()
    {
        assertEquals( "osx-aarch64", ImageMagickService.platform( "Mac OS X", "arm64" ) );
        assertEquals( "linux-x86_64", ImageMagickService.platform( "Linux", "amd64" ) );
        assertEquals( "linux-aarch64", ImageMagickService.platform( "Linux", "arm64" ) );
        assertEquals( "windows-aarch64", ImageMagickService.platform( "Windows 11", "aarch64" ) );
    }

    private ImageMagickService service( final String index, final AtomicInteger reads )
    {
        final Map<String, String> contents = Map.of( "files.txt", index, "magick.exe", "#!/bin/sh\nexit 0\n" );
        return new ImageMagickService( storage, "test", name -> {
            reads.incrementAndGet();
            final String value = contents.get( name.substring( "/native/imagemagick/test/".length() ) );
            return value == null ? null : new ByteArrayInputStream( value.getBytes( StandardCharsets.UTF_8 ) );
        } );
    }
}
