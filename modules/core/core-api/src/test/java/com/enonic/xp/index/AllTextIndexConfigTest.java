package com.enonic.xp.index;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;


class AllTextIndexConfigTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( AllTextIndexConfig.class ).withNonnullFields( "languages" ).verify();
    }
}
