package com.enonic.xp.portal.impl.postprocess;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import com.enonic.xp.portal.postprocess.PostProcessInjection;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.*;

public class PostProcessEvaluatorTest
{

    @Test
    public void testEvaluateSimpleHtml()
        throws Exception
    {
        final PostProcessEvaluator evaluator = new PostProcessEvaluator();
        evaluator.input = readResource( "postProcessEvalSource1.html" );
        evaluator.injections = Collections.emptyList();
        evaluator.instructions = Collections.emptyList();
        final String result = evaluator.evaluate();
        assertEqualsTrimmed( readResource( "postProcessEvalResult1.html" ), result );
    }

    @Test
    public void testEvaluateContributions()
        throws Exception
    {
        final PostProcessInjection contributionsInjection = ( portalRequest, portalResponse, tag ) -> {
            switch ( tag )
            {
                case HEAD_BEGIN:
                    return Arrays.asList( "<!-- HEAD BEGIN -->" );
                case HEAD_END:
                    return Arrays.asList( "<!-- HEAD END -->" );
                case BODY_BEGIN:
                    return Arrays.asList( "<!-- BODY BEGIN -->" );
                case BODY_END:
                    return Arrays.asList( "<!-- BODY END -->" );
                default:
                    return null;
            }
        };

        final PostProcessEvaluator evaluator = new PostProcessEvaluator();
        evaluator.input = readResource( "postProcessEvalSource2.html" );
        evaluator.injections = Lists.newArrayList( contributionsInjection );
        evaluator.instructions = Collections.emptyList();
        final String result = evaluator.evaluate();
        assertEqualsTrimmed( readResource( "postProcessEvalResult2.html" ), result );
    }

    @Test
    public void testEvaluateMultipleContributions()
        throws Exception
    {
        final PostProcessInjection contributionsInjection = ( portalRequest, portalResponse, tag ) -> {
            switch ( tag )
            {
                case HEAD_BEGIN:
                    return Arrays.asList( "<!-- HEAD BEGIN -->" );
                case HEAD_END:
                    return Arrays.asList( "<!-- HEAD END -->" );
                case BODY_BEGIN:
                    return Arrays.asList( "<!-- BODY BEGIN -->" );
                case BODY_END:
                    return Arrays.asList( "<!-- BODY END -->" );
                default:
                    return null;
            }
        };
        final PostProcessInjection contributionsInjection2 = ( portalRequest, portalResponse, tag ) -> {
            switch ( tag )
            {
                case HEAD_BEGIN:
                    return Arrays.asList( "<!-- HEAD BEGIN EXTRA-->" );
                case BODY_END:
                    return Arrays.asList( "<!-- BODY END EXTRA-->" );
                default:
                    return null;
            }
        };

        final PostProcessEvaluator evaluator = new PostProcessEvaluator();
        evaluator.input = readResource( "postProcessEvalSource4.html" );
        evaluator.injections = Lists.newArrayList( contributionsInjection, contributionsInjection2 );
        evaluator.instructions = Collections.emptyList();
        final String result = evaluator.evaluate();
        assertEqualsTrimmed( readResource( "postProcessEvalResult4.html" ), result );
    }

    @Test
    public void testEvaluateDuplicatedContributions()
        throws Exception
    {
        final PostProcessInjection contributionsInjection = ( portalRequest, portalResponse, tag ) -> {
            switch ( tag )
            {
                case HEAD_BEGIN:
                    return Arrays.asList( "<!-- HEAD BEGIN-->", "<!-- HEAD BEGIN DUPLICATED-1 -->", "<!-- HEAD BEGIN DUPLICATED-2 -->" );
                case HEAD_END:
                    return Arrays.asList( "<!-- HEAD END -->" );
                case BODY_BEGIN:
                    return Arrays.asList( "<!-- BODY BEGIN -->" );
                case BODY_END:
                    return Arrays.asList( "<!-- BODY END -->" );
                default:
                    return null;
            }
        };
        final PostProcessInjection contributionsInjection2 = ( portalRequest, portalResponse, tag ) -> {
            switch ( tag )
            {
                case HEAD_BEGIN:
                    return Arrays.asList( "<!-- HEAD BEGIN DUPLICATED-2 -->", "<!-- HEAD BEGIN DUPLICATED-1 -->" );
                case BODY_END:
                    return Arrays.asList( "<!-- BODY END -->" );
                default:
                    return null;
            }
        };

        final PostProcessEvaluator evaluator = new PostProcessEvaluator();
        evaluator.input = readResource( "postProcessEvalSource5.html" );
        evaluator.injections = Lists.newArrayList( contributionsInjection, contributionsInjection2 );
        evaluator.instructions = Collections.emptyList();
        final String result = evaluator.evaluate();
        assertEqualsTrimmed( readResource( "postProcessEvalResult5.html" ), result );
    }

    @Test
    public void testEvaluateInstructions()
        throws Exception
    {
        final PostProcessInstruction uppercaseInstruction = ( portalRequest, instruction ) -> {
            if ( instruction.startsWith( "UPPERCASE" ) )
            {
                return StringUtils.substringAfter( instruction, "UPPERCASE " ).toUpperCase();
            }
            return null;
        };
        final PostProcessInstruction expandInstruction = ( portalRequest, instruction ) -> {
            if ( instruction.startsWith( "EXPAND" ) )
            {
                return "<!--#UPPERCASE " + StringUtils.substringAfter( instruction, "EXPAND " ) + "-->";
            }
            return null;
        };

        final PostProcessEvaluator evaluator = new PostProcessEvaluator();
        evaluator.input = readResource( "postProcessEvalSource3.html" );
        evaluator.injections = Collections.emptyList();
        evaluator.instructions = Lists.newArrayList( uppercaseInstruction, expandInstruction );
        final String result = evaluator.evaluate();
        assertEqualsTrimmed( readResource( "postProcessEvalResult3.html" ), result );
    }

    private void assertEqualsTrimmed( final String expected, final String actual )
    {
        assertEquals( trimLines( expected ), trimLines( actual ) );
    }

    private String trimLines( final String text )
    {
        return text == null ? null : Stream.of( text.split( "\\r?\\n" ) ).
            map( String::trim ).
            collect( joining( "\r\n" ) );
    }

    private String readResource( final String resourceName )
        throws Exception
    {
        return Resources.toString( getClass().getResource( resourceName ), Charsets.UTF_8 );
    }

}