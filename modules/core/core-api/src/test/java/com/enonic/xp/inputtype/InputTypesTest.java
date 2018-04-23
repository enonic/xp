package com.enonic.xp.inputtype;

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
        assertEquals( 22, Lists.newArrayList( this.types.iterator() ).size() );
    }

    @Test
    public void resolveType()
    {
        final InputType type = this.types.resolve( InputTypeName.TEXT_LINE );
        assertNotNull( type );
        assertEquals( InputTypeName.TEXT_LINE.toString(), type.getName().toString() );
    }

    @Test
    public void resolveType_ignoreCase()
    {
        final InputType type = this.types.resolve( InputTypeName.from( "textline" ) );
        assertNotNull( type );
        assertEquals( InputTypeName.TEXT_LINE.toString(), type.getName().toString() );
    }

    @Test(expected = InputTypeNotFoundException.class)
    public void resolveType_unknown()
    {
        this.types.resolve( InputTypeName.from( "unknown" ) );
    }
}
