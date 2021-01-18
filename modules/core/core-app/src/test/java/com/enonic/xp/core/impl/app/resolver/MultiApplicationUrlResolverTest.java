package com.enonic.xp.core.impl.app.resolver;

import java.io.File;
import java.net.URL;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MultiApplicationUrlResolverTest
{
    @Mock
    private ApplicationUrlResolver delegate1;

    @Mock
    private ApplicationUrlResolver delegate2;

    private MultiApplicationUrlResolver resolver;

    @BeforeEach
    public void setup()
    {
        this.resolver = new MultiApplicationUrlResolver( this.delegate1, this.delegate2 );
    }

    @Test
    void testFindFiles()
    {
        when( this.delegate1.findFiles() ).thenReturn( Set.of( "a/b/c.txt", "a/other.txt" ) );
        when( this.delegate2.findFiles() ).thenReturn( Set.of( "a/other.txt", "a/b/other.txt" ) );

        final Set<String> files = this.resolver.findFiles();
        assertEquals( 3, files.size() );

        assertTrue( files.contains( "a/b/c.txt" ) );
        assertTrue( files.contains( "a/other.txt" ) );
        assertTrue( files.contains( "a/b/other.txt" ) );
    }

    @Test
    void testFindUrl()
        throws Exception
    {
        final URL expected1 = new File( "." ).toURI().toURL();
        when( this.delegate1.findUrl( "a/b.txt" ) ).thenReturn( expected1 );

        final URL expected2 = new File( "." ).toURI().toURL();
        when( this.delegate2.findUrl( "a/other.txt" ) ).thenReturn( expected2 );

        final URL url1 = this.resolver.findUrl( "a/b.txt" );
        assertSame( expected1, url1 );

        final URL url2 = this.resolver.findUrl( "a/other.txt" );
        assertSame( expected2, url2 );

        final URL url3 = this.resolver.findUrl( "other.txt" );
        assertNull( url3 );
    }

    @Test
    void filesHash()
    {
        when( this.delegate1.filesHash( "/a" ) ).thenReturn( Long.MIN_VALUE );

        when( this.delegate2.filesHash( "/a" ) ).thenReturn( Long.MAX_VALUE );

        long result = this.resolver.filesHash( "/a" );
        assertNotEquals( 0, result );
    }
}
