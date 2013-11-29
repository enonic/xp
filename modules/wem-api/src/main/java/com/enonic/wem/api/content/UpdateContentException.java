package com.enonic.wem.api.content;


import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.exception.BaseException;

public class UpdateContentException
    extends BaseException
{
    public UpdateContentException( final UpdateContent command, final Throwable t )
    {
        super( t, "Failed to update content [" + command.getContentId() + "]: " + t.getMessage() );
    }
}
