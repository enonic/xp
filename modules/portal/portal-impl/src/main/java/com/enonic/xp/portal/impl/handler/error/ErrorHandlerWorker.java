package com.enonic.xp.portal.impl.handler.error;

import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;

final class ErrorHandlerWorker
    extends PortalHandlerWorker
{
    protected HttpStatus code;

    protected String message;

    @Override
    public void execute()
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

        throw new WebException( this.code, this.message );
    }
}
