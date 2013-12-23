package com.enonic.wem.portal.postprocess;

import java.text.ParseException;


public final class PostProcessorString
    extends BasePostProcessor
{

    @Override
    protected String processStringResponse( final String responseBody )
        throws Exception
    {
        final StringBuilder result = new StringBuilder( responseBody );

        int pos = 0;
        int exprStart = responseBody.indexOf( "${", pos );

        while ( exprStart > -1 )
        {
            int exprEnd = result.indexOf( "}", exprStart + 2 );
            if ( exprEnd == -1 )
            {
                throw new ParseException( "Missing closing delimiter in expression", exprStart );
            }
            final String expr = result.substring( exprStart + 2, exprEnd );
            final String evaluated = expressionExecutor.evaluateExpression( expr );
            expressionCounter++;
            result.replace( exprStart, exprEnd + 1, evaluated );

            pos = exprStart + evaluated.length() + 1;
            exprStart = result.indexOf( "${", pos );
        }

        return result.toString();
    }
}
