package com.enonic.wem.portal.postprocess;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class PostProcessorRegExp
    extends BasePostProcessor
{
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile( "\\$\\{(.*?)\\}" );

    @Override
    protected String processStringResponse( final String responseBody )
        throws Exception
    {
        final Matcher matchPattern = EXPRESSION_PATTERN.matcher( responseBody );

        final StringBuffer sb = new StringBuffer( responseBody.length() );
        while ( matchPattern.find() )
        {
            final String expr = matchPattern.group( 1 );
            final String evaluated = expressionExecutor.evaluateExpression( expr );
            matchPattern.appendReplacement( sb, evaluated );
        }
        matchPattern.appendTail( sb );
        return sb.toString();
    }
}
