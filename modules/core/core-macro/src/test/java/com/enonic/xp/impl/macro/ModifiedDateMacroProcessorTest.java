package com.enonic.xp.impl.macro;

import org.junit.Test;

import com.enonic.xp.macro.MacroContext;
import com.enonic.xp.macro.MacroProcessor;

import static org.junit.Assert.*;

public class ModifiedDateMacroProcessorTest
{
    @Test
    public void testProcess()
    {

        final MacroProcessor macroProcessor = new ModifiedDateMacroProcessor();

        final MacroContext macroContext1 = MacroContext.create().name( "name" ).
            param( "modified_date", "2016-03-03T14:00:00Z" ).
            build();

        assertEquals( "2016-03-03 14:00:00", macroProcessor.process( macroContext1 ) );

        final MacroContext macroContext2 = MacroContext.create().name( "name" ).
            param( "modified_date", "2016-03-03T14:00:00Z" ).
            param( "modified_date_format", "yyyy-MM-dd HH:mm" ).
            build();

        assertEquals( "2016-03-03 14:00", macroProcessor.process( macroContext2 ) );

        final MacroContext macroContext3 = MacroContext.create().name( "name" ).
            param( "modified_date", "2016-03-03T14:00:00Z" ).
            param( "modified_date_format", "dd-MM-yy" ).
            build();

        assertEquals( "03-03-16", macroProcessor.process( macroContext3 ) );

        final MacroContext macroContext4 = MacroContext.create().name( "name" ).
            build();

        assertNull( macroProcessor.process( macroContext4 ) );
    }
}
