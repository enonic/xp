package com.enonic.wem.api.command.content.template;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.ImageTemplate;
import com.enonic.wem.api.content.page.ImageTemplateId;

public final class UpdateImageTemplate
    extends Command<Boolean>
{
    private ImageTemplateId templateId;

    private TemplateEditor<ImageTemplate> editor;

    public UpdateImageTemplate templateId( final ImageTemplateId templateId )
    {
        this.templateId = templateId;
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
