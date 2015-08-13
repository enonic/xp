package com.enonic.xp.form.inputtype;

import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class InputTypesTest
{
    private final InputTypes types;

    public InputTypesTest()
    {
        this.types = InputTypes.BUILTIN;
    }

    @Test
    public void countBuiltinTypes()
    {
        assertEquals( 21, Lists.newArrayList( this.types.iterator() ).size() );
    }

    @Test
    public void resolveType()
    {
        final InputType type = this.types.resolve( InputTypeName.TEXT_LINE );
        assertNotNull( type );
        assertEquals( InputTypeName.TEXT_LINE.toString(), type.getName() );
    }

    @Test(expected = InputTypeNotFoundException.class)
    public void resolveType_unknown()
    {
        this.types.resolve( InputTypeName.from( "unknown" ) );
    }
}
