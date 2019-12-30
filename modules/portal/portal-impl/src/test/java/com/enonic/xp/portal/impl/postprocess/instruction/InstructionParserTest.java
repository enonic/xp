package com.enonic.xp.portal.impl.postprocess.instruction;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class InstructionParserTest
{

    @Test
    public void parseInstruction()
        throws Exception
    {
        final Instruction instruction =
            new InstructionParser().parse( "MACRO attrib1=\"value1\" attrib2=\"escaped \\\\ \\\"string\\\" \"" );
        assertEquals( "MACRO", instruction.getId() );
        assertIterableEquals( List.of( "attrib1", "attrib2" ), instruction.attributeNames() );
        assertEquals( "value1", instruction.attribute( "attrib1" ) );
        assertEquals( "escaped \\ \"string\" ", instruction.attribute( "attrib2" ) );
    }

    @Test
    public void parseNoAttributes()
        throws Exception
    {
        final Instruction instruction = new InstructionParser().parse( "MACRO" );
        assertEquals( "MACRO", instruction.getId() );
        assertIterableEquals( List.of(), instruction.attributeNames() );
    }
}
