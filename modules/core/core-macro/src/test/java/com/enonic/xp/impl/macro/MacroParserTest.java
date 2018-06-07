package com.enonic.xp.impl.macro;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

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
        assertValidMacro( true, "[macro-na-me]with dashes in name[/macro-na-me]" );

        assertValidMacro( false, "1x" );
        assertValidMacro( false, "[macroName" );
        assertValidMacro( false, "macroName]" );
        assertValidMacro( false, "[macroName xxx]" );
        assertValidMacro( false, "[macroName]ooo" );
        assertValidMacro( false, "[macroName]ooo[/macroName" );
        assertValidMacro( false, "[]ooo[/]" );
        assertValidMacro( false, "[/]" );
        assertValidMacro( false, "[_/]" );
        assertValidMacro( false, "[-/]" );
    }

    @Test
    public void testMacroInBody()
    {
        final String test1 = "[macroName]body [macroInBody/] body[/macroName]";
        final MacroParser macroParser1 = new MacroParser();
        final Macro parsedMacro1 = macroParser1.parse( test1 );

        assertEquals( "[macroName]body [macroInBody/] body[/macroName]", parsedMacro1.toString() );

        final String test2 = "[macroName ][macroInBody ]body body[/macroInBody][/macroName]";
        final MacroParser macroParser2 = new MacroParser();
        final Macro parsedMacro2 = macroParser2.parse( test2 );

        assertEquals( "[macroName][macroInBody ]body body[/macroInBody][/macroName]", parsedMacro2.toString() );

        final String test3 = "[macroName par1=\"val1\"][macroInBody ]body body[/macroInBody][/macroName]";
        final MacroParser macroParser3 = new MacroParser();
        final Macro parsedMacro3 = macroParser3.parse( test3 );

        assertEquals( "[macroName par1=\"val1\"][macroInBody ]body body[/macroInBody][/macroName]", parsedMacro3.toString() );
    }

    @Test
    public void testParseWithBodyAndAttributes()
    {
        final String macro = "[macroName par1=\"val1\" par2=\"val2\" par3=\"val3\"]body body[/macroName]";
        final MacroParser parser = new MacroParser();
        final Macro parsedMacro = parser.parse( macro );

        assertEquals( "macroName", parsedMacro.getName() );
        assertEquals( "body body", parsedMacro.getBody() );
        assertEquals( 3, parsedMacro.getParameters().size() );
        assertEquals( "val1", first( parsedMacro.getParameter( "par1" ) ) );
        assertEquals( "val2", first( parsedMacro.getParameter( "par2" ) ) );
        assertEquals( "val3", first( parsedMacro.getParameter( "par3" ) ) );

        final String test2 = "[macroName ]body body[/macroName]";
        final MacroParser macroParser2 = new MacroParser();
        final Macro parsedMacro2 = macroParser2.parse( test2 );

        assertEquals( "[macroName]body body[/macroName]", parsedMacro2.toString() );
    }

    @Test
    public void testParseWithBody()
    {
        final String macro = "[macroName ]body body[/macroName]";
        final MacroParser parser = new MacroParser();
        final Macro parsedMacro = parser.parse( macro );

        assertEquals( "macroName", parsedMacro.getName() );
        assertEquals( "body body", parsedMacro.getBody() );
        assertEquals( 0, parsedMacro.getParameters().size() );
    }

    @Test
    public void testParseWithoutBodyAndAttributes()
    {
        final String macro = "[macroName par1=\"val1\" par2=\"val2\" par3=\"val3\"/]";
        final MacroParser parser = new MacroParser();
        final Macro parsedMacro = parser.parse( macro );

        assertEquals( "macroName", parsedMacro.getName() );
        assertEquals( "", parsedMacro.getBody() );
        assertEquals( 3, parsedMacro.getParameters().size() );
        assertEquals( "val1", first( parsedMacro.getParameter( "par1" ) ) );
        assertEquals( "val2", first( parsedMacro.getParameter( "par2" ) ) );
        assertEquals( "val3", first( parsedMacro.getParameter( "par3" ) ) );
    }

    @Test
    public void testParseWithoutBody()
    {
        final String macro = "[macroName /]";
        final MacroParser parser = new MacroParser();
        final Macro parsedMacro = parser.parse( macro );

        assertEquals( "macroName", parsedMacro.getName() );
        assertEquals( "", parsedMacro.getBody() );
        assertEquals( 0, parsedMacro.getParameters().size() );
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
        assertEquals( 2, parsedMacro.getParameters().size() );
        assertEquals( "value\"1", first( parsedMacro.getParameter( "par1" ) ) );
        assertEquals( "\\va\"l\"ue2", first( parsedMacro.getParameter( "par2" ) ) );
    }

    @Test
    public void testNameNotStartingWithUnderscore()
    {
        final String macro = "[_mymacro][/_mymacro]";
        try
        {
            new MacroParser().debugMode().parse( macro );

            fail( "Expected exception" );
        }
        catch ( ParseException e )
        {
            assertEquals( "Name cannot start with underscore '_' at position 1", e.getMessage() );
        }
    }

    @Test
    public void testAttributeNameNotStartingWithUnderscore()
    {
        final String macro = "[mymacro _body=\"something\"]real body[/mymacro]";
        try
        {
            new MacroParser().debugMode().parse( macro );

            fail( "Expected exception" );
        }
        catch ( ParseException e )
        {
            assertEquals( "Name cannot start with underscore '_' at position 9", e.getMessage() );
        }
    }

    @Test
    public void testValidMacroWithMultiValues()
    {
        final String macro = "[macroName par1=\"val1\" par1=\"val2\" par2=\"other1\" par2=\"other2\" par3=\"something\" /]";
        assertValidMacro( true, macro );

        final MacroParser parser = new MacroParser();
        final Macro parsedMacro = parser.parse( macro );

        assertEquals( "macroName", parsedMacro.getName() );
        assertEquals( "", parsedMacro.getBody() );
        assertEquals( 5, parsedMacro.getParameters().size() );
        assertEquals( 2, parsedMacro.getParameter( "par1" ).size() );
        assertEquals( 2, parsedMacro.getParameter( "par2" ).size() );
        assertEquals( 1, parsedMacro.getParameter( "par3" ).size() );

        assertEquals( "val1,val2", parsedMacro.getParameter( "par1" ).stream().collect( Collectors.joining( "," ) ) );
        assertEquals( "other1,other2", parsedMacro.getParameter( "par2" ).stream().collect( Collectors.joining( "," ) ) );
        assertEquals( "something", parsedMacro.getParameter( "par3" ).stream().collect( Collectors.joining( "," ) ) );
    }

    @Test
    public void testParseWithHtmlEncodedAttributes()
    {
        final String macro = "[macroName par1=\"&oslash;&aelig;&aring;\" par2=\"oea\"]body[/macroName]";
        final MacroParser parser = new MacroParser();
        final Macro parsedMacro = parser.parse( macro );

        assertEquals( "macroName", parsedMacro.getName() );
        assertEquals( "body", parsedMacro.getBody() );
        assertEquals( 2, parsedMacro.getParameters().size() );
        assertEquals( "øæå", first( parsedMacro.getParameter( "par1" ) ) );
        assertEquals( "oea", first( parsedMacro.getParameter( "par2" ) ) );
    }

    private <T> T first( final Collection<T> values )
    {
        final Iterator<T> ite = values.iterator();
        return ite.hasNext() ? ite.next() : null;
    }
}