package com.enonic.xp.portal.impl.postprocess.instruction;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Iterables;

import static org.junit.jupiter.api.Assertions.*;

public class InstructionParserTest
{

    @Test
    public void parseInstruction()
        throws Exception
    {
        final Instruction instruction =
            new InstructionParser().parse( "MACRO attrib1=\"value1\" attrib2=\"escaped \\\\ \\\"string\\\" \"" );
        assertEquals( "MACRO", instruction.getId() );
        assertEquals( 2, Iterables.size( instruction.attributeNames() ) );
        assertEquals( "value1", instruction.attribute( "attrib1" ) );
        assertEquals( "escaped \\ \"string\" ", instruction.attribute( "attrib2" ) );
    }

    @Test
    public void parseNoAttributes()
        throws Exception
    {
        final Instruction instruction = new InstructionParser().parse( "MACRO" );
        assertEquals( "MACRO", instruction.getId() );
        assertEquals( 0, Iterables.size( instruction.attributeNames() ) );
    }
}
