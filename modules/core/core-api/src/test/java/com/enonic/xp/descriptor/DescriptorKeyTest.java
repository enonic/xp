package com.enonic.xp.descriptor;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import com.enonic.xp.app.ApplicationKey;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DescriptorKeyTest
{
    @Test
    void equals()
    {
        EqualsVerifier.forClass( DescriptorKey.class ).verify();
    }

    @Test
    void space_in_name_not_allowed()
    {
        assertThatThrownBy( () -> DescriptorKey.from( ApplicationKey.from( "myapp" ), "my descriptor" ) ).isInstanceOf(
            IllegalArgumentException.class );
    }
}
