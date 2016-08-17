package com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.function;

class FunctionQueryBuilderException
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
        final String builder = "Illegal argument '" +
            illegalValue +
            "' in function '" +
            functionName +
            "', positon " +
            position;
        return builder;
    }
}
