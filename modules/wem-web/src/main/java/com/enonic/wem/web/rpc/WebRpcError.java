package com.enonic.wem.web.rpc;

public final class WebRpcError
{
    private final int code;

    private final int httpStatus;

    private final String message;

    private WebRpcError( final int code, final int httpStatus, final String message )
    {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getCode()
    {
        return this.code;
    }

    public int getHttpStatus()
    {
        return this.httpStatus;
    }

    public String getMessage()
    {
        return this.message;
    }

    public static WebRpcError methodNotFound( final String message )
    {
        return new WebRpcError( -32601, 404, "Method not found: " + message );
    }

    public static WebRpcError internalError( final String message )
    {
        return new WebRpcError( -32603, 500, "Internal Error: " + message );
    }

    public static WebRpcError invalidParams( final String message )
    {
        return new WebRpcError( -32602, 400, "Invalid params: " + message );
    }

    public static WebRpcError invalidRequest( final String message )
    {
        return new WebRpcError( -32600, 500, "Invalid request: " + message );
    }

    public static WebRpcError parseError( final String message )
    {
        return new WebRpcError( -32700, 500, "Parse error: " + message );
    }
}
