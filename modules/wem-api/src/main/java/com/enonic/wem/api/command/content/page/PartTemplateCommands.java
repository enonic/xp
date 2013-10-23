package com.enonic.wem.api.command.content.page;


public final class PartTemplateCommands
{
    private final PartTemplateGetCommands getCommands = new PartTemplateGetCommands();

    public PartTemplateGetCommands get()
    {
        return getCommands;
    }
}
