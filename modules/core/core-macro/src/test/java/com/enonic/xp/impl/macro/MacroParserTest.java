package com.enonic.xp.impl.macro;

import org.junit.Test;

import com.enonic.xp.macro.Macro;

import static org.junit.Assert.*;

public class MacroParserTest
{

    private void assertValidMacro( final boolean expectedValid, final String text )
    {
        try
        {
            final boolean isValid = new MacroParser().debugMode().parse( text ) != null;
            assertTrue( "Expected invalid macro: " + text, isValid && expectedValid );
        }
        catch ( ParseException e )
        {
            if ( expectedValid )
            {
                e.printStackTrace();
                fail( "Invalid macro: " + text + "\r\n" + e.getMessage() );
            }
        }
    }

    @Test
    public void testValidMacro()
    {
        assertValidMacro( true, "[macroName /]" );
        assertValidMacro( true, "[macroName/]" );
        assertValidMacro( false, "[macroName par1=val1 /]" );
        assertValidMacro( true, "[macroName par1=\"val1\" /]" );
        assertValidMacro( true, "[macroName par1=\"val1\" par2=\"val2\" /]" );
        assertValidMacro( true, "[macroName par1=\"val1\" par2=\"val2\"/]" );
        assertValidMacro( true, "[1111 111=\"111\" 111=\"111\" /]" );
        assertValidMacro( true, "[macroName par1=\"val1\" par2=\"val2\"]!!!????===[/macroName]" );

        assertValidMacro( true, "[macroName par1=\"val1\" par2=\"val2\"]text[/macroName]" );
        assertValidMacro( true, "[macroName par1=\"val1\" par2=\"val2\"] text [/macroName]" );
        assertValidMacro( false, "[macroName par1=\"val1\" par2=\"val2\"]text [/macroName2]" );
        assertValidMacro( false, "[macroName par1=\"val1\" par2=\"val2\"]!!!????===[/macroName2]" );
        assertValidMacro( true, "[macroName par1=\"val1\" par2=\"val2\"]!!!????===[/macroName]" );
        assertValidMacro( true, "[macro_name par1=\"?!--\" par2=\"---\"]text[/macro_name]" );
        assertValidMacro( true, "[macro_name][macro_in_body/][/macro_name]" );
        assertValidMacro( true, "[macro_name][macro_in_body/][/macro_in_body][/macro_name]" );
        assertValidMacro( true, "[macro_name][macro_in_body/][/macro[/macro_name]" );

        assertValidMacro( false, "1x" );
        assertValidMacro( false, "[macroName" );
        assertValidMacro( false, "macroName]" );
        assertValidMacro( false, "[macroName xxx]" );
        assertValidMacro( false, "[macroName]ooo" );
        assertValidMacro( false, "[macroName]ooo[/macroName" );
        assertValidMacro( false, "[]ooo[/]" );
        assertValidMacro( false, "[/]" );
        assertValidMacro( true, "[_/]" );
    }

    @Test
    public void testMacroInBody()
    {

        final String test1 = "[macroName]body [macroInBody/] body[/macroName]";
        final MacroParser macroParser1 = new MacroParser();
        final Macro parsedMacro1 = macroParser1.parse( test1 );

        assertEquals( "macroName=body [macroInBody/] body[]", parsedMacro1.toString() );

        final String test2 = "[macroName ][macroInBody ]body body[/macroInBody][/macroName]";
        final MacroParser macroParser2 = new MacroParser();
        final Macro parsedMacro2 = macroParser2.parse( test2 );

        assertEquals( "macroName=[macroInBody ]body body[/macroInBody][]", parsedMacro2.toString() );

        final String test3 = "[macroName par1=\"val1\"][macroInBody ]body body[/macroInBody][/macroName]";
        final MacroParser macroParser3 = new MacroParser();
        final Macro parsedMacro3 = macroParser3.parse( test3 );

        assertEquals( "macroName=[macroInBody ]body body[/macroInBody][par1=val1]", parsedMacro3.toString() );
    }

    @Test
    public void testParseWithBodyAndAttributes()
    {
        final String macro = "[macroName par1=\"val1\" par2=\"val2\" par3=\"val3\"]body body[/macroName]";
        final MacroParser parser = new MacroParser();
        final Macro parsedMacro = parser.parse( macro );

        assertEquals( "macroName", parsedMacro.getName() );
        assertEquals( "body body", parsedMacro.getBody() );
        assertEquals( 3, parsedMacro.getParams().size() );
        assertEquals( "val1", parsedMacro.getParam( "par1" ) );
        assertEquals( "val2", parsedMacro.getParam( "par2" ) );
        assertEquals( "val3", parsedMacro.getParam( "par3" ) );

        final String test2 = "[macroName ]body body[/macroName]";
        final MacroParser macroParser2 = new MacroParser();
        final Macro parsedMacro2 = macroParser2.parse( test2 );

        assertEquals( "macroName=body body[]", parsedMacro2.toString() );
    }

    @Test
    public void testParseWithBody()
    {
        final String macro = "[macroName ]body body[/macroName]";
        final MacroParser parser = new MacroParser();
        final Macro parsedMacro = parser.parse( macro );

        assertEquals( "macroName", parsedMacro.getName() );
        assertEquals( "body body", parsedMacro.getBody() );
        assertEquals( 0, parsedMacro.getParams().size() );
    }

    @Test
    public void testParseWithoutBodyAndAttributes()
    {
        final String macro = "[macroName par1=\"val1\" par2=\"val2\" par3=\"val3\"/]";
        final MacroParser parser = new MacroParser();
        final Macro parsedMacro = parser.parse( macro );

        assertEquals( "macroName", parsedMacro.getName() );
        assertEquals( "", parsedMacro.getBody() );
        assertEquals( 3, parsedMacro.getParams().size() );
        assertEquals( "val1", parsedMacro.getParam( "par1" ) );
        assertEquals( "val2", parsedMacro.getParam( "par2" ) );
        assertEquals( "val3", parsedMacro.getParam( "par3" ) );
    }

    @Test
    public void testParseWithoutBody()
    {
        final String macro = "[macroName /]";
        final MacroParser parser = new MacroParser();
        final Macro parsedMacro = parser.parse( macro );

        assertEquals( "macroName", parsedMacro.getName() );
        assertEquals( "", parsedMacro.getBody() );
        assertEquals( 0, parsedMacro.getParams().size() );
    }

    @Test
    public void testParseWithEscapedAttributes()
    {
        final String macro =
            "[macro_name123 par1 = \"value\\\"1\" par2 = \"\\\\va\\\"l\\\"ue2\"]/][body][/[/macro_name123 [/macro_name123]";
        final MacroParser parser = new MacroParser();
        final Macro parsedMacro = parser.parse( macro );

        assertEquals( "macro_name123", parsedMacro.getName() );
        assertEquals( "/][body][/[/macro_name123 ", parsedMacro.getBody() );
        assertEquals( 2, parsedMacro.getParams().size() );
        assertEquals( "value\"1", parsedMacro.getParam( "par1" ) );
        assertEquals( "\\va\"l\"ue2", parsedMacro.getParam( "par2" ) );
    }

}