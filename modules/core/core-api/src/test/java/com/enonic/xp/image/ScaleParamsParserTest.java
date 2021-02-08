package com.enonic.xp.image;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ScaleParamsParserTest
{

    @Test
    void simple()
    {
        final ScaleParams val = new ScaleParamsParser().parse( "a-1-2" );
        assertAll( () -> assertNotNull( val ), () -> assertEquals( "a(1,2)", val.toString() ), () -> assertEquals( "a", val.getName() ),
                   () -> assertArrayEquals( new Object[]{1, 2}, val.getArguments() ) );
    }

    @Test
    void noArg()
    {
        final ScaleParams val = new ScaleParamsParser().parse( "a" );
        assertAll( () -> assertNotNull( val ), () -> assertEquals( "a()", val.toString() ), () -> assertEquals( "a", val.getName() ),
                   () -> assertArrayEquals( new Object[0], val.getArguments() ) );
    }

    @Test
    void nullArg()
    {
        final ScaleParams val = new ScaleParamsParser().parse( "a--2" );
        assertAll( () -> assertNotNull( val ), () -> assertEquals( "a(,2)", val.toString() ), () -> assertEquals( "a", val.getName() ),
                   () -> assertArrayEquals( new Object[]{null, 2}, val.getArguments() ) );
    }

    @Test
    void noNullArgAfter()
    {
        final ScaleParams val = new ScaleParamsParser().parse( "a--" );
        assertAll( () -> assertNotNull( val ), () -> assertEquals( "a()", val.toString() ), () -> assertEquals( "a", val.getName() ),
                   () -> assertArrayEquals( new Object[0], val.getArguments() ) );
    }

    @Test
    void nullForNoname()
    {
        final ScaleParams val = new ScaleParamsParser().parse( "--" );
        assertNull( val );
    }

    @Test
    void nullForBlank()
    {
        final ScaleParams val = new ScaleParamsParser().parse( " " );
        assertNull( val );
    }
}
