package com.enonic.xp.form.inputtype;

import org.junit.Test;

import static org.junit.Assert.*;

public class InputTypeResolverTest
{
    @Test
    public void resolveType()
    {
        assertSame( InputTypes.TEXT_LINE, InputTypeResolver.get().resolve( InputTypes.TEXT_LINE.getName() ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveType_unknown()
    {
        InputTypeResolver.get().resolve( "Unknown" );
    }
}
