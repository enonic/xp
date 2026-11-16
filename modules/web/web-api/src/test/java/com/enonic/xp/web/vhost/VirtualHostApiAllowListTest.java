package com.enonic.xp.web.vhost;

import org.junit.jupiter.api.Test;

import com.enonic.xp.descriptor.DescriptorKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VirtualHostApiAllowListTest
{
    @Test
    void allows_listed_apis_and_every_api_of_listed_applications()
    {
        final VirtualHostApiAllowList list = VirtualHostApiAllowList.parse( "media:image, com.example.app:*" );

        assertThat( list.allows( DescriptorKey.from( "media:image" ) ) ).isTrue();
        assertThat( list.allows( DescriptorKey.from( "media:attachment" ) ) ).isFalse();
        assertThat( list.allows( DescriptorKey.from( "com.example.app:one" ) ) ).isTrue();
        assertThat( list.allows( DescriptorKey.from( "com.example.app:two" ) ) ).isTrue();
        assertThat( list.allows( DescriptorKey.from( "com.example.other:one" ) ) ).isFalse();
    }

    @Test
    void ignores_blank_entries()
    {
        final VirtualHostApiAllowList list = VirtualHostApiAllowList.parse( " media:image ,, " );

        assertThat( list.getApis() ).containsExactly( DescriptorKey.from( "media:image" ) );
        assertThat( list.getApplications() ).isEmpty();
    }

    @Test
    void rejects_empty_list()
    {
        assertThatThrownBy( () -> VirtualHostApiAllowList.parse( " , " ) ).isInstanceOf( IllegalArgumentException.class );
    }

    @Test
    void rejects_entry_without_api()
    {
        assertThatThrownBy( () -> VirtualHostApiAllowList.parse( "com.example.app" ) ).isInstanceOf( IllegalArgumentException.class );
    }

    @Test
    void rejects_wildcard_without_application()
    {
        assertThatThrownBy( () -> VirtualHostApiAllowList.parse( ":*" ) ).isInstanceOf( IllegalArgumentException.class );
    }

    @Test
    void rejects_invalid_application()
    {
        assertThatThrownBy( () -> VirtualHostApiAllowList.parse( "not an app:api" ) ).isInstanceOf( IllegalArgumentException.class );
    }
}
