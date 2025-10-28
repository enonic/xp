package com.enonic.xp.impl.macro;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Multimap;

import com.enonic.xp.macro.Macro;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlMacroEvaluatorTest
{
    @Test
    void macroInTag()
    {
        String result = testMacro( "<p>[macro test=\"123\"/]</p>", 1 );
        assertEquals( "<p>{macro test=\"123\"/}</p>", result );
    }

    @Test
    void macroInTagMultiAttr()
    {
        String result = testMacro( "<p class=\"foo\" title = \"bar\" >[macro test=\"123\"/]</p>", 1 );
        assertEquals( "<p class=\"foo\" title = \"bar\" >{macro test=\"123\"/}</p>", result );
    }

    @Test
    void macroInTagMultiValues()
    {
        String result =
            testMacro( "<p class=\"foo\" title = \"bar\" >[macro param1=\"123\" param1=\"456\" param1=\"789\" param2=\"abc\"/]</p>", 1 );
        assertEquals( "<p class=\"foo\" title = \"bar\" >{macro param1=\"123\" param1=\"456\" param1=\"789\" param2=\"abc\"/}</p>",
                      result );
    }

    @Test
    void macroSingleQuoteAttr()
    {
        String result = testMacro( "<p class='foo' title='bar'>[macro test=\"123\"/]</p>", 1 );
        assertEquals( "<p class='foo' title='bar'>{macro test=\"123\"/}</p>", result );
    }

    @Test
    void macroUnquotedAttr()
    {
        String result = testMacro( "<p checked title = bar readonly  class=foo>[macro test=\"123\"/]</p>", 1 );
        assertEquals( "<p checked title = bar readonly  class=foo>{macro test=\"123\"/}</p>", result );
    }

    @Test
    void macroUnquotedAttr2()
    {
        String result = testMacro( "<p checked/>[macro test=\"123\"/]</p>", 1 );
        assertEquals( "<p checked/>{macro test=\"123\"/}</p>", result );
    }

    @Test
    void selfClosingTag()
    {
        String result = testMacro( "<button/>[macro test=\"123\"/]  <span /> [macro test=\"123\"/]", 2 );
        assertEquals( "<button/>{macro test=\"123\"/}  <span /> {macro test=\"123\"/}", result );
    }

    @Test
    void macroInTags()
    {
        String result = testMacro( "<p>[macro test=\"123\"/]</p>  <span class=\"foo\"> [mymacro/]</span>", 2 );
        assertEquals( "<p>{macro test=\"123\"/}</p>  <span class=\"foo\"> {mymacro/}</span>", result );
    }

    @Test
    void macroInAttribute()
    {
        String result = testMacro( "<p test='[macro test=\"123\"/]'></p>", 0 );
        assertEquals( "<p test='[macro test=\"123\"/]'></p>", result );
    }

    @Test
    void nonClosingTags()
    {
        String result = testMacro( "<p><img  src  = \"img.jpg\">[macro test=\"123\"/]<span>  <span class=\"foo\"> [mymacro/]<img>", 2 );
        assertEquals( "<p><img  src  = \"img.jpg\">{macro test=\"123\"/}<span>  <span class=\"foo\"> {mymacro/}<img>", result );
    }

    @Test
    void ignoreEscapedMacro()
    {
        String result = testMacro( "<p>\\[macro test=\"123\"/]'></p>", 0 );
        assertEquals( "<p>\\[macro test=\"123\"/]'></p>", result );
    }

    @Test
    void macroInComments()
    {
        String result = testMacro( "<p><!-- [macro test=\"123\"/] --></p>", 0 );
        assertEquals( "<p><!-- [macro test=\"123\"/] --></p>", result );
    }

    @Test
    void macroInComments2()
    {
        String result = testMacro( "<p><!-- -- - [macro test=\"123\"/] --></p>[macro2/]<!---->[macro3/]", 2 );
        assertEquals( "<p><!-- -- - [macro test=\"123\"/] --></p>{macro2/}<!---->{macro3/}", result );
    }

    @Test
    void docType()
    {
        String result = testMacro( "<!DOCTYPE html><p>[macro test=\"123\"/]</p>", 1 );
        assertEquals( "<!DOCTYPE html><p>{macro test=\"123\"/}</p>", result );
    }

    @Test
    void cdata()
    {
        String result = testMacro( "<![CDATA[[macro test=\"123\"/]]]>[macro2/]", 1 );
        assertEquals( "<![CDATA[[macro test=\"123\"/]]]>{macro2/}", result );
    }

    @Test
    void invalidComments()
    {
        String result = testMacro( "<!wrong>[macro1/]</some <>[macro2/]", 2 );
        assertEquals( "<!wrong>{macro1/}</some <>{macro2/}", result );
    }

    @Test
    void bigHtmlContent()
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
        final Multimap<String, String> params = macro.getParameters();
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
