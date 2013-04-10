package com.enonic.wem.api.module;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ModuleNameTest
{
    @Test(expected = IllegalArgumentException.class)
    public void upper_case_is_illegal()
    {
        ModuleName.from( "UpperCaseIsIllegal" );
    }

    @Test
    public void lower_case_is_legal()
    {
        assertEquals( "lowercaseislegal", ModuleName.from( "lowercaseislegal" ).toString() );
    }
}
