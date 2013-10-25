package com.enonic.wem.core.rendering;


import java.io.IOException;
import java.nio.charset.Charset;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

public final class RenderingResult
{
    private final boolean success;

    private ByteSource result;

//    private int httpStatusCode;

    private RenderingResult( final Builder builder )
    {
        this.success = builder.success;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public boolean isError()
    {
        return !success;
    }

    public String getAsString()
    {
        try
        {
            return this.result.asCharSource( Charset.forName( "UFT-8" ) ).read();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    public ByteSource getResult()
    {
        return result;
    }

    public static RenderingResult.Builder newRenderingResult()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Boolean success;

        private ByteSource result;

        private Builder()
        {
        }

        public Builder success()
        {
            this.success = true;
            return this;
        }

        public Builder error()
        {
            this.success = false;
            return this;
        }

        public Builder result( final String value )
        {
            this.result = ByteStreams.asByteSource( value.getBytes( Charset.forName( "UTF-8" ) ) );
            return this;
        }

        public Builder result( final ByteSource source )
        {
            this.result = source;
            return this;
        }

        public RenderingResult build()
        {
            return new RenderingResult( this );
        }
    }
}
