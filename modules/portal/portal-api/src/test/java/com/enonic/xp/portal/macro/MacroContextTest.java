package com.enonic.xp.portal.macro;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MacroContextTest
{
    @Test
    void testCreation()
    {
        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            param( "param1", "value1" ).
            build();
        assertNotNull( macroContext );
    }

    @Test
    void testEquals()
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
    void testToString()
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
    void testMultipleValues()
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

        assertEquals( List.of( "value1", "value2", "value3" ), macroContext1.getParameter( "param1" ) );
        assertEquals( List.of( "other" ), macroContext1.getParameter( "param2" ) );

        assertEquals( 3, macroContext1.getParameter( "param1" ).size() );
        assertEquals( "value1", macroContext1.getParameter( "param1" ).get( 0 ) );
        assertEquals( "value3", macroContext1.getParameter( "param1" ).get( 2 ) );
        assertEquals( 1, macroContext1.getParameter( "param2" ).size() );
        assertEquals( "other", macroContext1.getParameter( "param2" ).get( 0 ) );

        assertEquals( 3, macroContext1.getParameter( "param1" ).size() );

        assertEquals( 4, macroContext1.getParameters().size() );

        assertEquals( "body", macroContext1.getBody() );
        assertEquals( "name", macroContext1.getName() );
    }
}
