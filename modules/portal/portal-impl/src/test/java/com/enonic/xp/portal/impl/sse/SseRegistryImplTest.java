package com.enonic.xp.portal.impl.sse;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SseRegistryImplTest
{
    private SseRegistryImpl registry;

    @BeforeEach
    void setup()
    {
        registry = new SseRegistryImpl();
    }

    @Test
    void addAndGet()
    {
        final SseEntry entry = mockEntry( "id1" );
        registry.add( entry );
        assertNotNull( registry.getById( "id1" ) );
    }

    @Test
    void remove()
    {
        final SseEntry entry = mockEntry( "id1" );
        registry.add( entry );
        registry.remove( entry );
        assertNull( registry.getById( "id1" ) );
    }

    @Test
    void getByGroup()
    {
        final SseEntry entry1 = mockEntry( "id1", "group1" );
        final SseEntry entry2 = mockEntry( "id2", "group1" );
        final SseEntry entry3 = mockEntry( "id3", "group2" );

        registry.add( entry1 );
        registry.add( entry2 );
        registry.add( entry3 );

        final List<SseEntry> group1 = registry.getByGroup( "group1" ).collect( Collectors.toList() );
        assertEquals( 2, group1.size() );

        final List<SseEntry> group2 = registry.getByGroup( "group2" ).collect( Collectors.toList() );
        assertEquals( 1, group2.size() );
    }

    @Test
    void getById_notFound()
    {
        assertNull( registry.getById( "nonexistent" ) );
    }

    private SseEntry mockEntry( final String id )
    {
        return mockEntry( id, null );
    }

    private SseEntry mockEntry( final String id, final String group )
    {
        final SseEntry entry = mock( SseEntry.class );
        when( entry.getId() ).thenReturn( id );
        if ( group != null )
        {
            when( entry.isInGroup( group ) ).thenReturn( true );
        }
        return entry;
    }
}
