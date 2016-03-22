package com.enonic.xp.impl.macro;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.macro.Macro;

import static org.junit.Assert.*;

public class MacroParserTest
{
    @Test
    public void testValidMacro() {

        assertTrue( MacroParser.isValidMacro( "[macroName /]" ) );
        assertTrue( MacroParser.isValidMacro( "[macroName/]" ) );
        assertTrue( MacroParser.isValidMacro( "[macroName par1=val1 /]" ) );
        assertTrue( MacroParser.isValidMacro( "[macroName par1=val1 par2=val2 /]" ) );
        assertTrue( MacroParser.isValidMacro( "[1111 111=111 111=111 /]" ) );

        assertTrue( MacroParser.isValidMacro( "[macroName par1=val1 par2=val2]text[/macroName]" ) );
        assertTrue( MacroParser.isValidMacro( "[macroName par1=val1 par2=val2] text [/macroName]" ) );
        assertTrue( MacroParser.isValidMacro( "[macroName par1=val1 par2=val2]text [/macroName2]" ) );
        assertTrue( MacroParser.isValidMacro( "[macroName par1=val1 par2=val2]!!!????===[/macroName2]" ) );

        assertFalse( MacroParser.isValidMacro( "1x" ) );
        assertFalse( MacroParser.isValidMacro( "[macroName" ) );
        assertFalse( MacroParser.isValidMacro( "macroName]" ) );
        assertFalse( MacroParser.isValidMacro( "[macroName xxx]" ) );
        assertFalse( MacroParser.isValidMacro( "[macroName]ooo" ) );
    }

    @Test
    public void testParseWithBody() {

        final String test1 = "[macroName par1=val1 par2=val2 par3=val3]body body[/macroName]";
        final MacroParser macroParser1 = new MacroParser( ApplicationKey.from("my-app") );
        final Macro parsedMacro1 = macroParser1.parse( test1 );

        assertEquals( "my-app:macroName=body body[par1=val1,par2=val2,par3=val3]", parsedMacro1.toString() );

        final String test2 = "[macroName ]body body[/macroName]";
        final MacroParser macroParser2 = new MacroParser( ApplicationKey.from("my-app") );
        final Macro parsedMacro2 = macroParser2.parse( test2 );

        assertEquals( "my-app:macroName=body body[]", parsedMacro2.toString() );
    }

    @Test
    public void testParseWithoutBody() {

        final String test1 = "[macroName par1=val1 par2=val2 par3=val3/]";
        final MacroParser macroParser1 = new MacroParser( ApplicationKey.from("my-app") );
        final Macro parsedMacro1 = macroParser1.parse( test1 );

        assertEquals( "my-app:macroName[par1=val1,par2=val2,par3=val3]", parsedMacro1.toString() );

        final String test2 = "[macroName /]";
        final MacroParser macroParser2 = new MacroParser( ApplicationKey.from("my-app") );
        final Macro parsedMacro2 = macroParser2.parse( test2 );

        assertEquals( "my-app:macroName[]", parsedMacro2.toString() );
    }
}
