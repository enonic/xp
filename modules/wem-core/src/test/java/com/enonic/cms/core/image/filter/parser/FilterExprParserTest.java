/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.filter.parser;

import junit.framework.TestCase;

public class FilterExprParserTest
    extends TestCase
{
    public void testEmpty()
    {
        assertEquals( "", parseAndSerialize( "" ) );
    }

    public void testNoArgs()
    {
        assertEquals( "some()", parseAndSerialize( "some" ) );
        assertEquals( "some()", parseAndSerialize( "some()" ) );
    }

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

    public void testMultiArgs()
    {
        assertEquals( "some(1,2)", parseAndSerialize( "some(1,2)" ) );
        assertEquals( "some(1,2,3)", parseAndSerialize( "some(1, 2, 3)" ) );
        assertEquals( "some(1,'2',3)", parseAndSerialize( "some(1, '2', 3)" ) );
        assertEquals( "some(1,true,false)", parseAndSerialize( "some(1,true,false)" ) );
    }

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
