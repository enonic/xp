package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.ImageTemplate;
import com.enonic.wem.api.content.page.ImageTemplateId;

public class GetImageTemplate
    extends Command<ImageTemplate>
{
    private ImageTemplateId id;

    public GetImageTemplate()
    {
    }

    public GetImageTemplate templateId( final ImageTemplateId id )
    {
        this.id = id;
        return this;
    }

    public ImageTemplateId getId()
    {
        return id;
    }

    @Override
    public void validate()
    {

    }
}
