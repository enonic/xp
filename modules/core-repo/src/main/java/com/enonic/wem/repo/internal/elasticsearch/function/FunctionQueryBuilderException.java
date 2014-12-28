package com.enonic.wem.repo.internal.elasticsearch.function;

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

    public FunctionQueryBuilderException( final String functionName, final int position, final String illegalValue, final Throwable t )
    {
        super( createMessage( functionName, position, illegalValue ), t );
    }


    private static String createMessage( final String functionName, final int position, final String illegalValue )
    {
        StringBuilder builder = new StringBuilder();
        builder.append( "Illegal argument '" ).
            append( illegalValue ).
            append( "' in function '" ).
            append( functionName ).
            append( "', positon " ).
            append( position );
        return builder.toString();
    }
}
