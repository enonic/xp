package com.enonic.wem.core.index.query.function;

public class FunctionQueryBuilderException
    extends RuntimeException
{

    public FunctionQueryBuilderException( final String message )
    {
        super( message );
    }

    public FunctionQueryBuilderException( final String functionName, final int position, final String illegalValue )
    {
        super( createMessage( functionName, position, illegalValue ) );
    }

    private static String createMessage( final String functionName, final int position, final String illegalValue )
    {
        StringBuilder builder = new StringBuilder();
        builder.append( "Illegal argument '" + illegalValue + "' in function '" + functionName + "', positon " + position );
        return builder.toString();
    }
}
