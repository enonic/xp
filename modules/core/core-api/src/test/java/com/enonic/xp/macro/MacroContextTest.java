package com.enonic.xp.macro;

    import org.junit.Test;

    import static org.junit.Assert.*;

public class MacroContextTest
{
    @Test
    public void testCreation()
    {
        final MacroContext macroContext = MacroContext.create( "name", "body" ).param( "param1", "value1" ).build();
        assertEquals( "name=body[param1=value1]", macroContext.toString() );
    }

    @Test
    public void testEquals()
    {
        final MacroContext macroContext1 =
            MacroContext.create( "name", "body" ).param( "param1", "value1" ).param( "param2", "value2" ).build();

        final MacroContext macroContext2 =
            MacroContext.create( "name", "body" ).param( "param1", "value1" ).param( "param2", "value2" ).build();

        final MacroContext macroContext3 =
            MacroContext.create( "name", "body" ).param( "param1", "value1" ).param( "param2", "value3" ).build();

        assertEquals( macroContext1, macroContext2 );
        assertNotEquals( macroContext1, macroContext3 );
    }
}
