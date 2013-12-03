package com.enonic.wem.admin.rest.resource;

public class Result
{
    private final Exception exception;

    private final ErrorJson error;

    private final Object result;

    protected Result( Object result, final Exception exception, final String errorMessage )
    {
        this.result = result;

        if ( exception != null )
        {
            exception.printStackTrace();
            this.exception = exception;
            this.error = new ErrorJson( exception.getMessage() );
        }
        else if ( errorMessage != null )
        {
            this.exception = null;
            this.error = new ErrorJson( errorMessage );
        }
        else
        {
            this.exception = null;
            error = null;
        }
    }

    public ErrorJson getError()
    {
        return error;
    }

    public Object getResult()
    {
        return this.result;
    }

    public static Result exception( final Exception exception )
    {
        return new Result( null, exception, null );
    }

    public static Result error( final String message )
    {
        return new Result( null, null, message );
    }

    public static Result result( final Object object )
    {
        return new Result( object, null, null );
    }
}
