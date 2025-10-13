package com.enonic.xp.inputtype;

import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertEquals( 23, StreamSupport.stream( this.types.spliterator(), false ).count() );
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

    @Test
    public void resolveType_unknown()
    {
        assertThrows( InputTypeNotFoundException.class, () -> this.types.resolve( InputTypeName.from( "unknown" ) ) );
    }
}
