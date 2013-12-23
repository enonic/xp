package com.enonic.wem.portal.postprocess;


import java.util.Date;
import java.util.Random;

import org.junit.Test;

import com.google.common.base.Stopwatch;

import com.enonic.wem.portal.controller.JsHttpResponse;

import static junit.framework.Assert.assertEquals;

public class PostProcessorTest
{
    private final static String INPUT = generateInput( 5000, 10 );

    @Test
    public void testPostProcessRegExp()
        throws Exception
    {
        final PostProcessorRegExp postProcessor = new PostProcessorRegExp();
        postProcessor.expressionExecutor = new DummyExpressionExecutor();

        final JsHttpResponse response = new JsHttpResponse();
        response.setBody( INPUT );

        final Stopwatch timer = new Stopwatch().start();
        postProcessor.processResponse( response );
        timer.stop();
        System.out.println( "Processing time for " + postProcessor.getClass().getSimpleName() + " : " + timer.toString() );
        System.out.println( INPUT.length() + " characters, " + postProcessor.getExpressionCounter() + " expressions evaluated with " +
                                postProcessor.expressionExecutor.getClass().getSimpleName() );
//        assertEquals( INPUT, response.getBody() );
    }

    @Test
    public void testPostProcessSubString()
        throws Exception
    {
        final PostProcessorString postProcessor = new PostProcessorString();
        postProcessor.expressionExecutor = new DummyExpressionExecutor();

        final JsHttpResponse response = new JsHttpResponse();
        response.setBody( INPUT );

        final Stopwatch timer = new Stopwatch().start();
        postProcessor.processResponse( response );
        timer.stop();
        System.out.println( "Processing time for " + postProcessor.getClass().getSimpleName() + " : " + timer.toString() );
        System.out.println( INPUT.length() + " characters, " + postProcessor.getExpressionCounter() + " expressions evaluated with " +
                                postProcessor.expressionExecutor.getClass().getSimpleName() );
//        assertEquals( INPUT, response.getBody() );
    }

    @Test
    public void testPostProcessSubStringJavaEl()
        throws Exception
    {
        final PostProcessorString postProcessor = new PostProcessorString();
        postProcessor.expressionExecutor = new JavaElExpressionExecutor( false );

        final JsHttpResponse response = new JsHttpResponse();
        response.setBody( INPUT );

        final Stopwatch timer = new Stopwatch().start();
        postProcessor.processResponse( response );
        timer.stop();
        System.out.println( "Processing time for " + postProcessor.getClass().getSimpleName() + "(NOT cached) : " + timer.toString() );
        System.out.println( INPUT.length() + " characters, " + postProcessor.getExpressionCounter() + " expressions evaluated with " +
                                postProcessor.expressionExecutor.getClass().getSimpleName() );
//        assertEquals( INPUT, response.getBody() );
    }

    @Test
    public void testPostProcessSubStringJavaElCached()
        throws Exception
    {
        final PostProcessorString postProcessor = new PostProcessorString();
        postProcessor.expressionExecutor = new JavaElExpressionExecutor( true );

        final JsHttpResponse response = new JsHttpResponse();
        response.setBody( INPUT );

        final Stopwatch timer = new Stopwatch().start();
        postProcessor.processResponse( response );
        timer.stop();
        System.out.println( "Processing time for " + postProcessor.getClass().getSimpleName() + "(CACHED) : " + timer.toString() );
        System.out.println( INPUT.length() + " characters, " + postProcessor.getExpressionCounter() + " expressions evaluated with " +
                                postProcessor.expressionExecutor.getClass().getSimpleName() );
//        assertEquals( INPUT, response.getBody() );
    }

    @Test
    public void testPostProcess()
        throws Exception
    {
        final String inputBody = generateInput( 10000 );

        // Reg exp
        final PostProcessorRegExp postProcessorRegExp = new PostProcessorRegExp();
        postProcessorRegExp.expressionExecutor = new JavaElExpressionExecutor( true );

        final JsHttpResponse response = new JsHttpResponse();
        response.setBody( inputBody );
        postProcessorRegExp.processResponse( response );
        final String processedWithRegExp = (String) response.getBody();

        // Substring
        final PostProcessorString postProcessorString = new PostProcessorString();
        postProcessorString.expressionExecutor = new JavaElExpressionExecutor( false );

        final JsHttpResponse response2 = new JsHttpResponse();
        response2.setBody( inputBody );
        postProcessorString.processResponse( response2 );
        final String processedWithSubString = (String) response2.getBody();

        assertEquals( processedWithRegExp, processedWithSubString );
    }

    private String generateInput( final int wordCount )
    {
        return generateInput( wordCount, 50 );
    }

    private static String generateInput( final int wordCount, final int percentageExpressions )
    {
        final String[] words = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};

        final Random random = new Random( new Date().getTime() );
        final StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < wordCount; i++ )
        {
            final int rnd = random.nextInt( words.length );
//            final boolean isExpr = random.nextBoolean();
            final boolean isExpr = random.nextInt( 100 ) <= percentageExpressions;
            final String word = words[rnd];
            if ( isExpr )
            {
                final String expr = "portal:createUrl('" + word + "')";
                sb.append( "${" ).append( expr ).append( "} " );
            }
            else
            {
                sb.append( word ).append( " " );
            }
            if ( ( i + 1 ) % 10 == 0 )
            {
                sb.append( "\r\n" );
            }
        }
        return sb.toString();
    }
}
