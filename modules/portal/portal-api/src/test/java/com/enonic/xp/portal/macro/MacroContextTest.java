package com.enonic.xp.portal.macro;

import org.junit.Test;

import static org.junit.Assert.*;

public class MacroContextTest
{
    @Test
    public void testCreation()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "param1", "value1" ).
            build();
        assertNotNull( macroContext );
    }

    @Test
    public void testEquals()
    {
        final MacroContext macroContext1 = MacroContext.create().name( "name" ).
            body( "body" ).param( "param1", "value1" ).
            param( "param2", "value2" ).
            build();

        final MacroContext macroContext2 = MacroContext.create().name( "name" ).
            body( "body" ).param( "param1", "value1" ).
            param( "param2", "value2" ).
            build();

        final MacroContext macroContext3 = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "param1", "value1" ).
            param( "param2", "value3" ).
            build();

        assertEquals( macroContext1, macroContext2 );
        assertNotEquals( macroContext1, macroContext3 );
    }

    @Test
    public void testToString()
    {
        final MacroContext macroContext1 = MacroContext.create().name( "name" ).
            body( "body" ).
            document( "document" ).
            param( "param1", "value1" ).
            param( "param1", "value2" ).
            param( "param1", "value3" ).
            param( "param2", "other" ).
            build();

        assertEquals( "MacroContext{name=name, body=body, params={param1=[value1, value2, value3], param2=[other]}, " +
                          "request=null, document=document}", macroContext1.toString() );
    }

    @Test
    public void testMultipleValues()
    {
        final MacroContext macroContext1 = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "param1", "value1" ).
            param( "param1", "value2" ).
            param( "param1", "value3" ).
            param( "param2", "other" ).
            build();

        assertEquals( MacroContext.copyOf( macroContext1 ).build(), macroContext1 );
        assertEquals( MacroContext.copyOf( macroContext1 ).build().hashCode(), macroContext1.hashCode() );

        assertEquals( "value1,value2,value3", macroContext1.getParam( "param1" ) );
        assertEquals( "other", macroContext1.getParam( "param2" ) );

        assertEquals( 3, macroContext1.getParameter( "param1" ).size() );
        assertEquals( "value1", macroContext1.getParameter( "param1" ).get( 0 ) );
        assertEquals( "value3", macroContext1.getParameter( "param1" ).get( 2 ) );
        assertEquals( 1, macroContext1.getParameter( "param2" ).size() );
        assertEquals( "other", macroContext1.getParameter( "param2" ).get( 0 ) );

        assertEquals( 3, macroContext1.getParameter( "param1" ).size() );
        assertEquals( 2, macroContext1.getParams().size() );

        assertEquals( 4, macroContext1.getParameters().size() );

        assertEquals( "body", macroContext1.getBody() );
        assertEquals( "name", macroContext1.getName() );
    }
}
