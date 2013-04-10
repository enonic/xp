package com.enonic.wem.api.content;


import com.enonic.wem.api.command.content.RenameContent;
import com.enonic.wem.api.exception.BaseException;

public class RenameContentException
    extends BaseException
{
    public RenameContentException( final RenameContent renameContent, final Throwable t )
    {
        super( "Failed to rename content [" + renameContent.getContentId() + "] to '" + renameContent.getNewName() + "': " + t.getMessage(),
               t );
    }
}
