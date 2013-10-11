package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.template.GetTemplate;
import com.enonic.wem.api.command.template.TemplateGetCommands;
import com.enonic.wem.api.content.page.PartTemplate;
import com.enonic.wem.api.content.page.TemplateId;

public final class PartTemplateGetCommands
    extends TemplateGetCommands<PartTemplate>
{
    @Override
    public GetTemplate<PartTemplate> byId( final TemplateId id )
    {
        return super.byId( id );
    }
}
