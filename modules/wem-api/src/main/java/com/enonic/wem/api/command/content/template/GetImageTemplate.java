package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.ImageTemplate;
import com.enonic.wem.api.content.page.ImageTemplateName;

public class GetImageTemplate
    extends Command<ImageTemplate>
{
    private ImageTemplateName name;

    public GetImageTemplate()
    {
    }

    public GetImageTemplate templateName( final ImageTemplateName name )
    {
        this.name = name;
        return this;
    }

    public ImageTemplateName getId()
    {
        return name;
    }

    @Override
    public void validate()
    {

    }
}
