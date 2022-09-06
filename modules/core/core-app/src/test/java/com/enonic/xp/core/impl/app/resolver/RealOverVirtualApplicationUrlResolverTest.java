package com.enonic.xp.core.impl.app.resolver;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.enonic.xp.resource.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RealOverVirtualApplicationUrlResolverTest
{
    @Mock
    private ApplicationUrlResolver real;

    @Mock
    private ApplicationUrlResolver virtual;

    private RealOverVirtualApplicationUrlResolver resolver;

    @BeforeEach
    public void setup()
    {
        this.resolver = new RealOverVirtualApplicationUrlResolver( this.real, this.virtual );
    }

    @Test
    void testFindFiles()
    {
        when( this.real.findFiles() ).thenReturn( Set.of( "a/b/c.txt", "a/other.txt" ) );
        when( this.virtual.findFiles() ).thenReturn( Set.of( "a/other.txt", "a/b/other.txt" ) );

        final Set<String> files = this.resolver.findFiles();
        assertEquals( 2, files.size() );

        assertTrue( files.contains( "a/b/c.txt" ) );
        assertTrue( files.contains( "a/other.txt" ) );
        assertFalse( files.contains( "a/b/other.txt" ) );
    }

    @Test
    void testFindResource()
        throws Exception
    {
        final Resource expected1 = mock( Resource.class );
        when( this.real.findResource( "a/b.txt" ) ).thenReturn( expected1 );

        final Resource expected2 = mock( Resource.class );
        when( this.real.findResource( "a/other.txt" ) ).thenReturn( expected2 );

        final Resource expected3 = mock( Resource.class );
        when( this.virtual.findResource( "a/other.txt" ) ).thenReturn( expected3 );

        final Resource resource1 = this.resolver.findResource( "a/b.txt" );
        assertSame( expected1, resource1 );

        final Resource resource2 = this.resolver.findResource( "a/other.txt" );
        assertSame( expected2, resource2 );

        final Resource resource3 = this.resolver.findResource( "a/b/other.txt" );
        assertNull( resource3 );
    }

    @Test
    void testVirtual()
        throws Exception
    {
        this.resolver = new RealOverVirtualApplicationUrlResolver( null, this.virtual );

        final Resource expected1 = mock( Resource.class );
        when( this.virtual.findResource( "a/other.txt" ) ).thenReturn( expected1 );

        final Resource resource1 = this.resolver.findResource( "a/other.txt" );
        assertSame( expected1, resource1 );

        final Resource resource2 = this.resolver.findResource( "a/b.txt" );
        assertNull( resource2 );

        when( this.virtual.findFiles() ).thenReturn( Set.of( "a/other.txt", "a/b/other.txt" ) );

        final Set<String> files = this.resolver.findFiles();
        assertEquals( 2, files.size() );

        assertTrue( files.contains( "a/other.txt" ) );
        assertTrue( files.contains( "a/b/other.txt" ) );
    }

    @Test
    void testEmpty()
        throws Exception
    {
        this.resolver = new RealOverVirtualApplicationUrlResolver( null, null );

        final Resource resource = this.resolver.findResource( "a/other.txt" );
        assertNull( resource );

    }
}
