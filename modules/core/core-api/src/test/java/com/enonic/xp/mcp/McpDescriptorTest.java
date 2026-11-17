package com.enonic.xp.mcp;

import org.junit.jupiter.api.Test;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class McpDescriptorTest
{
    @Test
    void preserves_key_and_declared_principals()
    {
        final var key = DescriptorKey.from( "myapp:search" );
        final var principals = PrincipalKeys.from( "role:system.admin" );
        final var descriptor = new McpDescriptor( key, principals );

        assertThat( descriptor.getKey() ).isEqualTo( key );
        assertThat( descriptor.getAllowedPrincipals() ).isEqualTo( principals );
    }

    @Test
    void accepts_empty_principals()
    {
        final var descriptor = new McpDescriptor( DescriptorKey.from( "myapp:search" ), PrincipalKeys.empty() );

        assertThat( descriptor.getAllowedPrincipals() ).isEmpty();
    }

    @Test
    void rejects_null_metadata()
    {
        assertThatThrownBy( () -> new McpDescriptor( null, PrincipalKeys.empty() ) ).isInstanceOf( NullPointerException.class );
        assertThatThrownBy( () -> new McpDescriptor( DescriptorKey.from( "myapp:search" ), null ) )
            .isInstanceOf( NullPointerException.class );
    }
}
