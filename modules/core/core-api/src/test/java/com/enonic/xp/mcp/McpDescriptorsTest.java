package com.enonic.xp.mcp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class McpDescriptorsTest
{
    @Test
    void empty_factories_share_empty_collection()
    {
        assertThat( McpDescriptors.empty() ).isEmpty();
        assertThat( McpDescriptors.from() ).isSameAs( McpDescriptors.empty() );
        assertThat( McpDescriptors.from( List.of() ) ).isSameAs( McpDescriptors.empty() );
        assertThat( Stream.<McpDescriptor>empty().collect( McpDescriptors.collector() ) ).isSameAs( McpDescriptors.empty() );
    }

    @Test
    void copies_iterable_and_preserves_order()
    {
        final var first = descriptor( "first" );
        final var second = descriptor( "second" );
        final var source = new ArrayList<>( List.of( first, second ) );
        final var descriptors = McpDescriptors.from( source );
        source.clear();

        assertThat( descriptors ).containsExactly( first, second );
        assertThat( descriptors.getSize() ).isEqualTo( 2 );
        assertThat( descriptors.get( 0 ) ).isSameAs( first );
        assertThat( McpDescriptors.from( descriptors ) ).isSameAs( descriptors );
        assertThatThrownBy( () -> descriptors.getList().clear() ).isInstanceOf( UnsupportedOperationException.class );
        final var iterator = descriptors.iterator();
        iterator.next();
        assertThatThrownBy( iterator::remove ).isInstanceOf( UnsupportedOperationException.class );
    }

    @Test
    void copies_array_and_collects_in_encounter_order()
    {
        final var first = descriptor( "first" );
        final var second = descriptor( "second" );
        final var source = new McpDescriptor[]{first, second};
        final var descriptors = McpDescriptors.from( source );
        source[0] = second;

        assertThat( descriptors ).containsExactly( first, second );
        assertThat( Stream.of( first, second ).parallel().collect( McpDescriptors.collector() ) ).isEqualTo( descriptors );
    }

    @Test
    void rejects_null_descriptors()
    {
        assertThatThrownBy( () -> McpDescriptors.from( new McpDescriptor[]{null} ) ).isInstanceOf( NullPointerException.class );
        assertThatThrownBy( () -> McpDescriptors.from( Arrays.asList( descriptor( "first" ), null ) ) ).isInstanceOf(
            NullPointerException.class );
        assertThatThrownBy( () -> Stream.of( (McpDescriptor) null ).collect( McpDescriptors.collector() ) ).isInstanceOf(
            NullPointerException.class );
    }

    private static McpDescriptor descriptor( final String name )
    {
        return new McpDescriptor( DescriptorKey.from( "myapp:" + name ), PrincipalKeys.empty() );
    }
}
