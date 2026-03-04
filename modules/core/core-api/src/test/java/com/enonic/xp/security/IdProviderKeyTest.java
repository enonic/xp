package com.enonic.xp.security;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdProviderKeyTest
{
    @Test
    void equals()
    {
        EqualsVerifier.forClass( IdProviderKey.class ).verify();
    }

    @Test
    void space_in_name_not_allowed()
    {
        assertThatThrownBy( () -> IdProviderKey.from( "my provider" ) ).isInstanceOf( IllegalArgumentException.class );
    }
}