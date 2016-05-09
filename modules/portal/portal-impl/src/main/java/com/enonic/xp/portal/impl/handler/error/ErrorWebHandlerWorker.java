package com.enonic.xp.portal.impl.handler.error;

import com.enonic.xp.portal.PortalWebResponse;
import com.enonic.xp.portal.handler.PortalWebHandlerWorker;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.handler.WebException;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;

final class ErrorWebHandlerWorker
    extends PortalWebHandlerWorker
{
    private HttpStatus code;

    private String message;

    private ErrorWebHandlerWorker( final Builder builder )
    {
        super( builder );
        code = builder.code;
        message = builder.message;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public PortalWebResponse execute()
    {
        if ( this.code == null )
        {
            this.code = HttpStatus.NOT_FOUND;
        }

        if ( this.message == null )
        {
            this.message = this.code.getReasonPhrase();
        }

        throw new WebException( this.code, this.message );
    }

    public static final class Builder
        extends PortalWebHandlerWorker.Builder<Builder, WebRequest, WebResponse>
    {
        private HttpStatus code;

        private String message;

        private Builder()
        {
        }

        public Builder code( final HttpStatus code )
        {
            this.code = code;
            return this;
        }

        public Builder message( final String message )
        {
            this.message = message;
            return this;
        }

        public ErrorWebHandlerWorker build()
        {
            return new ErrorWebHandlerWorker( this );
        }
    }
}
