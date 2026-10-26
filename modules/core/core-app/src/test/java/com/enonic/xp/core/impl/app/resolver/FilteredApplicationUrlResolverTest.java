package com.enonic.xp.core.impl.app.resolver;

import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FilteredApplicationUrlResolverTest
{
    @Test
    void findFiles_filtered()
    {
        final ApplicationUrlResolver delegate = mock( ApplicationUrlResolver.class );
        when( delegate.findFiles() ).thenReturn( Set.of( "a.txt", "b.yaml" ) );

        final Predicate<String> include = path -> !path.endsWith( ".yaml" );
        final FilteredApplicationUrlResolver resolver = new FilteredApplicationUrlResolver( delegate, () -> include );

        assertEquals( Set.of( "a.txt" ), resolver.findFiles() );
    }

    @Test
    void findResource_included()
    {
        final ApplicationUrlResolver delegate = mock( ApplicationUrlResolver.class );
        final Resource resource = mock( Resource.class );
        when( delegate.findResource( "/a.txt" ) ).thenReturn( resource );

        final Predicate<String> include = path -> !path.endsWith( ".yaml" );
        final FilteredApplicationUrlResolver resolver = new FilteredApplicationUrlResolver( delegate, () -> include );

        assertSame( resource, resolver.findResource( "/a.txt" ) );
    }

    @Test
    void findResource_excluded()
    {
        final ApplicationUrlResolver delegate = mock( ApplicationUrlResolver.class );

        final Predicate<String> include = path -> !path.endsWith( ".yaml" );
        final FilteredApplicationUrlResolver resolver = new FilteredApplicationUrlResolver( delegate, () -> include );

        assertNull( resolver.findResource( "/b.yaml" ) );
        verify( delegate, never() ).findResource( "/b.yaml" );
    }
}
