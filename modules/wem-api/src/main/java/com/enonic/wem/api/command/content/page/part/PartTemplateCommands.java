package com.enonic.wem.api.command.content.page.part;


public final class PartTemplateCommands
{
    public CreatePartTemplate create()
    {
        return new CreatePartTemplate();
    }

    public UpdatePartTemplate update()
    {
        return new UpdatePartTemplate();
    }

    public DeletePartTemplate delete()
    {
        return new DeletePartTemplate();
    }

    public GetPartTemplateByKey getByKey()
    {
        return new GetPartTemplateByKey();
    }

    public GetPartTemplatesBySiteTemplate getBySiteTemplate()
    {
        return new GetPartTemplatesBySiteTemplate();
    }

}
