package com.enonic.xp.web.vhost;

import org.junit.jupiter.api.Test;

import com.enonic.xp.descriptor.DescriptorKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VirtualHostApiFilterTest
{
    @Test
    void accepts_listed_apis_and_prefixes()
    {
        final VirtualHostApiFilter filter = VirtualHostApiFilter.parse( "media:image, com.example.app:*" );

        assertThat( filter.accept( DescriptorKey.from( "media:image" ) ) ).isTrue();
        assertThat( filter.accept( DescriptorKey.from( "media:attachment" ) ) ).isFalse();
        assertThat( filter.accept( DescriptorKey.from( "com.example.app:one" ) ) ).isTrue();
        assertThat( filter.accept( DescriptorKey.from( "com.example.other:one" ) ) ).isFalse();
    }

    @Test
    void first_matching_pattern_decides()
    {
        final VirtualHostApiFilter filter = VirtualHostApiFilter.parse( "!com.example.app:internal, *" );

        assertThat( filter.accept( DescriptorKey.from( "com.example.app:internal" ) ) ).isFalse();
        assertThat( filter.accept( DescriptorKey.from( "com.example.app:public" ) ) ).isTrue();
        assertThat( filter.accept( DescriptorKey.from( "media:image" ) ) ).isTrue();
    }

    @Test
    void rejects_empty_filter()
    {
        assertThatThrownBy( () -> VirtualHostApiFilter.parse( " , " ) ).isInstanceOf( IllegalArgumentException.class );
    }

    @Test
    void rejects_pattern_without_api()
    {
        assertThatThrownBy( () -> VirtualHostApiFilter.parse( "com.example.app" ) ).isInstanceOf( IllegalArgumentException.class );
        assertThatThrownBy( () -> VirtualHostApiFilter.parse( "!com.example.app" ) ).isInstanceOf( IllegalArgumentException.class );
    }

    @Test
    void rejects_invalid_application()
    {
        assertThatThrownBy( () -> VirtualHostApiFilter.parse( "not an app:api" ) ).isInstanceOf( IllegalArgumentException.class );
    }
}
