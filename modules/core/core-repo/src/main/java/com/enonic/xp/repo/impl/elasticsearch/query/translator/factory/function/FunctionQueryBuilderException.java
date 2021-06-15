package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

class FunctionQueryBuilderException
    extends RuntimeException
{

    FunctionQueryBuilderException( final String message )
    {
        super( message );
    }

    FunctionQueryBuilderException( final String functionName, final int position, final String illegalValue )
    {
        super( createMessage( functionName, position, illegalValue ) );
    }

    FunctionQueryBuilderException( final String functionName, final int position, final String illegalValue, final Throwable t )
    {
        super( createMessage( functionName, position, illegalValue ), t );
    }


    private static String createMessage( final String functionName, final int position, final String illegalValue )
    {
        return String.format( "Illegal argument '%s' in function '%s', position %d", illegalValue, functionName, position );
    }
}
