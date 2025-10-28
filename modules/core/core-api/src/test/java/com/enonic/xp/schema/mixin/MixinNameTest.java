package com.enonic.xp.schema.mixin;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class MixinNameTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( MixinName.class ).usingGetClass().withNonnullFields( "applicationKey", "localName" ).verify();
    }
}
