package com.enonic.xp.schema.mixin;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class MixinNameTest
{
    @Test
    public void equalsContract()
    {
        EqualsVerifier.forClass( FormFragmentName.class ).usingGetClass().withNonnullFields( "applicationKey", "localName" ).verify();
    }
}
