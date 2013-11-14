package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.ImageTemplate;
import com.enonic.wem.api.content.page.ImageTemplateName;

public final class UpdateImageTemplate
    extends Command<Boolean>
{
    private ImageTemplateName templateName;

    private TemplateEditor<ImageTemplate> editor;

    public UpdateImageTemplate withName( final ImageTemplateName templateName )
    {
        this.templateName = templateName;
        return this;
    }

    public UpdateImageTemplate editor( final TemplateEditor<ImageTemplate> editor )
    {
        this.editor = editor;
        return this;
    }

    @Override
    public void validate()
    {
    }
}
