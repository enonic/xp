/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.image.parser;

import org.junit.Assert;
import org.junit.Test;

public class FilterExprParserTest
    extends Assert
{
    @Test
    public void testEmpty()
    {
        assertEquals( "", parseAndSerialize( "" ) );
    }

    @Test
    public void testNoArgs()
    {
        assertEquals( "some()", parseAndSerialize( "some" ) );
        assertEquals( "some()", parseAndSerialize( "some()" ) );
    }

    @Test
    public void testOneArg()
    {
        assertEquals( "some(1)", parseAndSerialize( "some(1)" ) );
        assertEquals( "some(-1)", parseAndSerialize( "some(-1)" ) );
        assertEquals( "some(1.0)", parseAndSerialize( "some(1.0)" ) );
        assertEquals( "some(-1.0)", parseAndSerialize( "some(-1.0)" ) );
        assertEquals( "some(false)", parseAndSerialize( "some(false)" ) );
        assertEquals( "some(true)", parseAndSerialize( "some(true)" ) );
        assertEquals( "some('1')", parseAndSerialize( "some('1')" ) );
        assertEquals( "some('1')", parseAndSerialize( "some(\"1\")" ) );
        assertEquals( "some(1)", parseAndSerialize( "some(0x1)" ) );
    }

    @Test
    public void testMultiArgs()
    {
        assertEquals( "some(1,2)", parseAndSerialize( "some(1,2)" ) );
        assertEquals( "some(1,2,3)", parseAndSerialize( "some(1, 2, 3)" ) );
        assertEquals( "some(1,'2',3)", parseAndSerialize( "some(1, '2', 3)" ) );
        assertEquals( "some(1,true,false)", parseAndSerialize( "some(1,true,false)" ) );
    }

    @Test
    public void testMultiExpr()
    {
        assertEquals( "aa();bb();cc()", parseAndSerialize( "aa;bb();cc" ) );
        assertEquals( "aa(1);bb(2);cc(3)", parseAndSerialize( "aa(1);bb(2);cc(3)" ) );
        assertEquals( "aa(1,'a');bb(2,'b');cc(3,'c')", parseAndSerialize( "aa(1,'a');bb(2,'b');cc(3,'c')" ) );
    }

    private String parseAndSerialize( String expr )
    {
        return new FilterExprParser().parse( expr ).toString();
    }
}
