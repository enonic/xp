package com.enonic.wem.api.content;


import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.exception.BaseException;

public class CreateContentException
    extends BaseException
{
    public CreateContentException( final CreateContent command, final Throwable t )
    {
        super( "Failed to create content [" + command.getDisplayName() + "] at path [" + command.getParentContentPath() + "]: " +
                   t.getMessage() );
    }

}
