package com.enonic.cms.web.rest.common;


public class RestResponse<T>
{

    private boolean success;

    private String status;

    private String error;

    private String msg;

    private T results;

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess( boolean success )
    {
        this.success = success;
    }

    public String getError()
    {
        return error;
    }

    public void setError( String error )
    {
        this.error = error;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg( String msg )
    {
        this.msg = msg;
    }

    public T getResults()
    {
        return results;
    }

    public void setResults( T results )
    {
        this.results = results;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus( String status )
    {
        this.status = status;
    }
}
