package com.enonic.xp.impl.macro;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableMultimap;

import com.enonic.xp.macro.Macro;

import static org.junit.Assert.*;

public class HtmlMacroEvaluatorTest
{
    @Test
    public void macroInTag()
        throws Exception
    {
        String result = testMacro( "<p>[macro test=\"123\"/]</p>", 1 );
        assertEquals( "<p>{macro test=\"123\"/}</p>", result );
    }

    @Test
    public void macroInTagMultiAttr()
        throws Exception
    {
        String result = testMacro( "<p class=\"foo\" title = \"bar\" >[macro test=\"123\"/]</p>", 1 );
        assertEquals( "<p class=\"foo\" title = \"bar\" >{macro test=\"123\"/}</p>", result );
    }

    @Test
    public void macroInTagMultiValues()
        throws Exception
    {
        String result =
            testMacro( "<p class=\"foo\" title = \"bar\" >[macro param1=\"123\" param1=\"456\" param1=\"789\" param2=\"abc\"/]</p>", 1 );
        assertEquals( "<p class=\"foo\" title = \"bar\" >{macro param1=\"123\" param1=\"456\" param1=\"789\" param2=\"abc\"/}</p>",
                      result );
    }

    @Test
    public void macroSingleQuoteAttr()
        throws Exception
    {
        String result = testMacro( "<p class='foo' title='bar'>[macro test=\"123\"/]</p>", 1 );
        assertEquals( "<p class='foo' title='bar'>{macro test=\"123\"/}</p>", result );
    }

    @Test
    public void macroUnquotedAttr()
        throws Exception
    {
        String result = testMacro( "<p checked title = bar readonly  class=foo>[macro test=\"123\"/]</p>", 1 );
        assertEquals( "<p checked title = bar readonly  class=foo>{macro test=\"123\"/}</p>", result );
    }

    @Test
    public void macroUnquotedAttr2()
        throws Exception
    {
        String result = testMacro( "<p checked/>[macro test=\"123\"/]</p>", 1 );
        assertEquals( "<p checked/>{macro test=\"123\"/}</p>", result );
    }

    @Test
    public void selfClosingTag()
        throws Exception
    {
        String result = testMacro( "<button/>[macro test=\"123\"/]  <span /> [macro test=\"123\"/]", 2 );
        assertEquals( "<button/>{macro test=\"123\"/}  <span /> {macro test=\"123\"/}", result );
    }

    @Test
    public void macroInTags()
        throws Exception
    {
        String result = testMacro( "<p>[macro test=\"123\"/]</p>  <span class=\"foo\"> [mymacro/]</span>", 2 );
        assertEquals( "<p>{macro test=\"123\"/}</p>  <span class=\"foo\"> {mymacro/}</span>", result );
    }

    @Test
    public void macroInAttribute()
        throws Exception
    {
        String result = testMacro( "<p test=\'[macro test=\"123\"/]\'></p>", 0 );
        assertEquals( "<p test='[macro test=\"123\"/]'></p>", result );
    }

    @Test
    public void nonClosingTags()
        throws Exception
    {
        String result = testMacro( "<p><img  src  = \"img.jpg\">[macro test=\"123\"/]<span>  <span class=\"foo\"> [mymacro/]<img>", 2 );
        assertEquals( "<p><img  src  = \"img.jpg\">{macro test=\"123\"/}<span>  <span class=\"foo\"> {mymacro/}<img>", result );
    }

    @Test
    public void ignoreEscapedMacro()
        throws Exception
    {
        String result = testMacro( "<p>\\[macro test=\"123\"/]\'></p>", 0 );
        assertEquals( "<p>\\[macro test=\"123\"/]'></p>", result );
    }

    @Test
    public void macroInComments()
        throws Exception
    {
        String result = testMacro( "<p><!-- [macro test=\"123\"/] --></p>", 0 );
        assertEquals( "<p><!-- [macro test=\"123\"/] --></p>", result );
    }

    @Test
    public void macroInComments2()
        throws Exception
    {
        String result = testMacro( "<p><!-- -- - [macro test=\"123\"/] --></p>[macro2/]<!---->[macro3/]", 2 );
        assertEquals( "<p><!-- -- - [macro test=\"123\"/] --></p>{macro2/}<!---->{macro3/}", result );
    }

    @Test
    public void docType()
        throws Exception
    {
        String result = testMacro( "<!DOCTYPE html><p>[macro test=\"123\"/]</p>", 1 );
        assertEquals( "<!DOCTYPE html><p>{macro test=\"123\"/}</p>", result );
    }

    @Test
    public void cdata()
        throws Exception
    {
        String result = testMacro( "<![CDATA[[macro test=\"123\"/]]]>[macro2/]", 1 );
        assertEquals( "<![CDATA[[macro test=\"123\"/]]]>{macro2/}", result );
    }

    @Test
    public void invalidComments()
        throws Exception
    {
        String result = testMacro( "<!wrong>[macro1/]</some <>[macro2/]", 2 );
        assertEquals( "<!wrong>{macro1/}</some <>{macro2/}", result );
    }

    @Test
    public void bigHtmlContent()
        throws Exception
    {
        final StringBuilder input = new StringBuilder();
        final StringBuilder expectedResult = new StringBuilder();
        for ( int i = 0; i < 2000; i++ )
        {
            input.append( "<aTag>Content<aSubTag attribute=\"value[notAMacro/]\"/>[myMacro/]</aTag>" );
            expectedResult.append( "<aTag>Content<aSubTag attribute=\"value[notAMacro/]\"/>{myMacro/}</aTag>" );
        }
        String result = testMacro( input.toString(), 2000 );
        assertEquals( expectedResult.toString(), result );
    }

    private String testMacro( final String macroText, final int expectedMacros )
        throws Exception
    {
        final List<Macro> macros = new ArrayList<>();
        HtmlMacroEvaluator macroService = new HtmlMacroEvaluator( macroText, ( macro ) -> {
            macros.add( macro );
            return macroToString( macro );
        } );

        final String result = macroService.evaluate();
        assertEquals( expectedMacros, macros.size() );
        return result;
    }

    private String macroToString( final Macro macro )
    {
        final StringBuilder result = new StringBuilder( "{" ).append( macro.getName() );
        final ImmutableMultimap<String, String> params = macro.getParameters();
        if ( params.isEmpty() && macro.getBody().isEmpty() )
        {
            result.append( "/}" );
        }
        else
        {
            for ( String paramName : params.keySet() )
            {
                for ( String value : params.get( paramName ) )
                {
                    result.append( " " ).append( paramName ).append( "=\"" );
                    result.append( escapeParam( value ) );
                    result.append( "\"" );
                }
            }
            if ( macro.getBody().isEmpty() )
            {
                result.append( "/}" );
            }
            else
            {
                result.append( "}" ).append( macro.getBody() ).append( "{/" ).append( macro.getName() ).append( "}" );
            }
        }
        return result.toString();
    }

    private String escapeParam( final String value )
    {
        return value.replace( "\\", "\\\\" ).replace( "\"", "\\\"" );
    }
}