package com.enonic.xp.repo.impl;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class InternalContextTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( InternalContext.class ).usingGetClass().withNonnullFields( "repositoryId", "branch" ).verify();
    }
}
