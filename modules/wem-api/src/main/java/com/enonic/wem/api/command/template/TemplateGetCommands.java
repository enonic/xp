package com.enonic.wem.api.command.template;


import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.TemplateId;

public class TemplateGetCommands<T extends Template>
{
    public GetTemplate<T> byId( TemplateId id )
    {
        return createCommand( id );
    }

    protected GetTemplate<T> createCommand( TemplateId id )
    {
        return new GetTemplate<>( id );
    }
}
