package com.enonic.xp.schema.formfragment;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class FormFragmentNameTest
{
    @Test
    public void equalsContract()
    {
        EqualsVerifier.forClass( FormFragmentName.class ).usingGetClass().withNonnullFields( "applicationKey", "localName" ).verify();
    }
}
