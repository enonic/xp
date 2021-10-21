package com.enonic.xp.portal.impl.handler.error;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

final class ErrorHandlerWorker
    extends PortalHandlerWorker<WebRequest>
{
    HttpStatus code;

    String message;

    ErrorHandlerWorker( final WebRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {
        if ( this.code == null )
        {
            this.code = HttpStatus.NOT_FOUND;
        }

        if ( this.message == null )
        {
            this.message = this.code.getReasonPhrase();
        }

        throw new WebException( this.code, this.message, false );
    }
}
