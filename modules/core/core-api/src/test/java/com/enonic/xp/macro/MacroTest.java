package com.enonic.xp.macro;

import org.junit.Test;

import static org.junit.Assert.*;

public class MacroTest
{
    @Test
    public void testCreation()
    {
        final Macro macro = Macro.create().name( "macro" ).
            body( "body" ).
            param( "param1", "value1" ).
            build();
        assertEquals( "[macro param1=\"value1\"]body[/macro]", macro.toString() );
    }

    @Test
    public void testBodyNull()
    {
        final Macro macro = Macro.create().name( "macro" ).param( "param1", "value1" ).build();
        assertEquals( "[macro param1=\"value1\"/]", macro.toString() );
    }

    @Test
    public void testEquals()
    {
        final Macro macro1 = Macro.create().name( "macro" ).
            body( "body" ).
            param( "param1", "value1" ).
            param( "param2", "value2" ).
            build();

        final Macro macro2 = Macro.create().name( "macro" ).
            body( "body" ).
            param( "param1", "value1" ).
            param( "param2", "value2" ).
            build();

        final Macro macro3 = Macro.create().name( "macro" ).
            body( "body" ).
            param( "param1", "value1" ).
            param( "param2", "value3" ).
            build();

        assertEquals( macro1, macro2 );
        assertNotEquals( macro1, macro3 );
    }

    @Test
    public void testMultipleValues()
    {
        final Macro macro = Macro.create().name( "macro" ).
            body( "body" ).
            param( "param1", "value1" ).
            param( "param1", "value2" ).
            param( "param1", "value3" ).
            param( "param2", "other" ).
            build();
        assertEquals( "[macro param1=\"value1\" param1=\"value2\" param1=\"value3\" param2=\"other\"]body[/macro]", macro.toString() );

        assertEquals( Macro.copyOf( macro ).build(), macro );
        assertEquals( Macro.copyOf( macro ).build().hashCode(), macro.hashCode() );

        assertEquals( "value1,value2,value3", macro.getParam( "param1" ) );
        assertEquals( "other", macro.getParam( "param2" ) );

        assertEquals( 3, macro.getParameter( "param1" ).size() );
        assertEquals( "value1", macro.getParameter( "param1" ).get( 0 ) );
        assertEquals( "value3", macro.getParameter( "param1" ).get( 2 ) );
        assertEquals( 1, macro.getParameter( "param2" ).size() );
        assertEquals( "other", macro.getParameter( "param2" ).get( 0 ) );

        assertEquals( 3, macro.getParameter( "param1" ).size() );
        assertEquals( 2, macro.getParams().size() );

        assertEquals( 4, macro.getParameters().size() );

        assertEquals( "body", macro.getBody() );
        assertEquals( "macro", macro.getName() );

    }
}
