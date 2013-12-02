package com.enonic.wem.api.command.content.site;

import com.enonic.wem.api.content.Content;

public class CreateSiteResult
{
    private final Content content;

    private final String message;

    private CreateSiteResult( final Content content )
    {
        this.content = content;
        this.message = null;
    }

    private CreateSiteResult( final String message )
    {
        this.message = message;
        this.content = null;
    }

    public static CreateSiteResult success( final Content content ) {
        return new CreateSiteResult( content );
    }

    public static CreateSiteResult error( final String message ) {
        return new CreateSiteResult( message );
    }

    public Content getContent()
    {
        return content;
    }

    public String getMessage()
    {
        return message;
    }
}
