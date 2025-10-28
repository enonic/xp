package com.enonic.xp.core.impl.app.resolver;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.resource.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
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
    void setup()
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
    void testFindResource()
    {
        final Resource expected1 = mock( Resource.class );
        when( this.delegate1.findResource( "a/b.txt" ) ).thenReturn( expected1 );

        final Resource expected2 = mock( Resource.class );
        when( this.delegate2.findResource( "a/other.txt" ) ).thenReturn( expected2 );

        final Resource resource1 = this.resolver.findResource( "a/b.txt" );
        assertSame( expected1, resource1 );

        final Resource resource2 = this.resolver.findResource( "a/other.txt" );
        assertSame( expected2, resource2 );

        final Resource resource3 = this.resolver.findResource( "other.txt" );
        assertNull( resource3 );
    }
}
