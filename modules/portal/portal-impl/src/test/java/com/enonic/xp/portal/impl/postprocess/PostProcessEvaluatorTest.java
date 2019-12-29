package com.enonic.xp.portal.impl.postprocess;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.portal.postprocess.PostProcessInjection;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        evaluator.portalResponse = PortalResponse.create().build();
        final PortalResponse result = evaluator.evaluate();
        assertEqualsTrimmed( readResource( "postProcessEvalResult1.html" ), result.getAsString() );
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
        evaluator.injections = List.of( contributionsInjection );
        evaluator.instructions = Collections.emptyList();
        evaluator.portalResponse = PortalResponse.create().build();
        final PortalResponse result = evaluator.evaluate();
        assertEqualsTrimmed( readResource( "postProcessEvalResult2.html" ), result.getAsString() );
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
        evaluator.injections = List.of( contributionsInjection, contributionsInjection2 );
        evaluator.instructions = Collections.emptyList();
        evaluator.portalResponse = PortalResponse.create().build();
        final PortalResponse result = evaluator.evaluate();
        assertEqualsTrimmed( readResource( "postProcessEvalResult4.html" ), result.getAsString() );
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
        evaluator.injections = List.of( contributionsInjection, contributionsInjection2 );
        evaluator.instructions = Collections.emptyList();
        evaluator.portalResponse = PortalResponse.create().build();
        final PortalResponse result = evaluator.evaluate();
        assertEqualsTrimmed( readResource( "postProcessEvalResult5.html" ), result.getAsString() );
    }

    @Test
    public void testEvaluateInstructionsAndContributions()
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
        evaluator.injections = List.of( contributionsInjection );
        evaluator.instructions = Collections.emptyList();
        evaluator.portalResponse = PortalResponse.create().build();
        evaluator.evaluateInstructions();
        final PortalResponse result = evaluator.evaluateContributions();
        assertEqualsTrimmed( readResource( "postProcessEvalResult2.html" ), result.getAsString() );
    }

    @Test
    public void testEvaluateInstructions()
        throws Exception
    {
        final PostProcessInstruction uppercaseInstruction = ( portalRequest, instruction ) -> {
            if ( instruction.startsWith( "UPPERCASE " ) )
            {
                return PortalResponse.create().
                    body( instruction.substring( "UPPERCASE ".length() ).toUpperCase() ).
                    build();
            }
            return null;
        };
        final PostProcessInstruction expandInstruction = ( portalRequest, instruction ) -> {
            if ( instruction.startsWith( "EXPAND " ) )
            {
                return PortalResponse.create().
                    body( "<!--#UPPERCASE " + instruction.substring( "EXPAND ".length() ) + "-->" ).
                    build();
            }
            return null;
        };

        final PostProcessEvaluator evaluator = new PostProcessEvaluator();
        evaluator.input = readResource( "postProcessEvalSource3.html" );
        evaluator.injections = Collections.emptyList();
        evaluator.instructions = List.of( uppercaseInstruction, expandInstruction );
        evaluator.portalResponse = PortalResponse.create().build();
        final PortalResponse result = evaluator.evaluate();
        assertEqualsTrimmed( readResource( "postProcessEvalResult3.html" ), result.getAsString() );
    }

    @Test
    public void testEvaluateInstructionSetCookie()
        throws Exception
    {
        final PostProcessInstruction setCookieInstruction = ( portalRequest, instruction ) -> {
            if ( instruction.startsWith( "INSTRUCTION" ) )
            {
                return PortalResponse.create().
                    cookie( new Cookie( "cookie-name", "cookie-value" ) ).
                    build();
            }
            return null;
        };

        final PostProcessEvaluator evaluator = new PostProcessEvaluator();
        evaluator.input = readResource( "postProcessEvalSource6.html" );
        evaluator.injections = Collections.emptyList();
        evaluator.instructions = List.of( setCookieInstruction );
        evaluator.portalResponse = PortalResponse.create().build();
        final PortalResponse result = evaluator.evaluate();
        assertEquals( 1, result.getCookies().size() );
        assertEquals( "cookie-name", result.getCookies().get( 0 ).getName() );
        assertEquals( "cookie-value", result.getCookies().get( 0 ).getValue() );
    }

    @Test
    public void testEvaluateInstructionSetHeader()
        throws Exception
    {
        final PostProcessInstruction setCookieInstruction = ( portalRequest, instruction ) -> {
            if ( instruction.startsWith( "INSTRUCTION" ) )
            {
                return PortalResponse.create().
                    header( "header-name", "header-value" ).
                    build();
            }
            return null;
        };

        final PostProcessEvaluator evaluator = new PostProcessEvaluator();
        evaluator.input = readResource( "postProcessEvalSource6.html" );
        evaluator.injections = Collections.emptyList();
        evaluator.instructions = List.of( setCookieInstruction );
        evaluator.portalResponse = PortalResponse.create().build();
        final PortalResponse result = evaluator.evaluate();
        assertEquals( 1, result.getHeaders().size() );
        assertEquals( "header-value", result.getHeaders().get( "header-name" ) );
    }

    @Test
    public void testEvaluateInstructionSetApplyFilters()
        throws Exception
    {
        final PostProcessInstruction setCookieInstruction = ( portalRequest, instruction ) -> {
            if ( instruction.startsWith( "INSTRUCTION" ) )
            {
                return PortalResponse.create().
                    applyFilters( false ).
                    build();
            }
            return null;
        };

        final PostProcessEvaluator evaluator = new PostProcessEvaluator();
        evaluator.input = readResource( "postProcessEvalSource6.html" );
        evaluator.injections = Collections.emptyList();
        evaluator.instructions = List.of( setCookieInstruction );
        evaluator.portalResponse = PortalResponse.create().build();
        final PortalResponse result = evaluator.evaluate();
        assertEquals( false, result.applyFilters() );
    }

    @Test
    public void testEvaluateInstructionSetContribution()
        throws Exception
    {
        final PostProcessInstruction setCookieInstruction = ( portalRequest, instruction ) -> {
            if ( instruction.startsWith( "INSTRUCTION" ) )
            {
                return PortalResponse.create().
                    contribution( HtmlTag.BODY_END, "<script src='my-script.js'/>" ).
                    build();
            }
            return null;
        };

        final PostProcessEvaluator evaluator = new PostProcessEvaluator();
        evaluator.input = readResource( "postProcessEvalSource6.html" );
        evaluator.injections = Collections.emptyList();
        evaluator.instructions = List.of( setCookieInstruction );
        evaluator.portalResponse = PortalResponse.create().build();
        final PortalResponse result = evaluator.evaluate();
        assertEquals( 1, result.getContributions( HtmlTag.BODY_END ).size() );
        assertEquals( "<script src='my-script.js'/>", result.getContributions( HtmlTag.BODY_END ).get( 0 ) );
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
        try (final InputStream stream = getClass().getResourceAsStream( resourceName ))
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
    }

}
