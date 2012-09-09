package com.enonic.wem.web.json.rpc;

public final class JsonRpcError
{
    private final int code;

    private final int httpStatus;

    private final String message;

    private JsonRpcError( final int code, final int httpStatus, final String message )
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

    public static JsonRpcError methodNotFound( final String message )
    {
        return new JsonRpcError( -32601, 404, "Method not found: " + message );
    }

    public static JsonRpcError internalError( final String message )
    {
        return new JsonRpcError( -32603, 500, "Internal Error: " + message );
    }

    public static JsonRpcError invalidParams( final String message )
    {
        return new JsonRpcError( -32602, 400, "Invalid params: " + message );
    }

    public static JsonRpcError invalidRequest( final String message )
    {
        return new JsonRpcError( -32600, 500, "Invalid request: " + message );
    }

    public static JsonRpcError parseError( final String message )
    {
        return new JsonRpcError( -32700, 500, "Parse error: " + message );
    }
}
