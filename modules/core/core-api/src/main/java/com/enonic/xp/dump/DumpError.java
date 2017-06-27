package com.enonic.xp.dump;

public class DumpError
{
    private final DumpErrorType type;

    private final String message;

    public enum DumpErrorType
    {
        BINARY_NOT_FOUND,
        VERSION_NOT_FOUND,
        OTHER
    }

    private DumpError( final DumpErrorType type, final String message )
    {
        this.type = type;
        this.message = message;
    }

    public static DumpError binaryNotFound( final String msg )
    {
        return new DumpError( DumpErrorType.BINARY_NOT_FOUND, msg );
    }

    public static DumpError versionNotFound( final String msg )
    {
        return new DumpError( DumpErrorType.VERSION_NOT_FOUND, msg );
    }

    public static DumpError error( final String msg )
    {
        return new DumpError( DumpErrorType.OTHER, msg );
    }

    public DumpErrorType getType()
    {
        return type;
    }

    public String getMessage()
    {
        return message;
    }
}
