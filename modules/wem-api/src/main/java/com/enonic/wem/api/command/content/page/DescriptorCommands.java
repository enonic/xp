package com.enonic.wem.api.command.content.page;


import com.enonic.wem.api.command.content.page.layout.LayoutDescriptorCommands;
import com.enonic.wem.api.command.content.page.part.PartDescriptorCommands;

public final class DescriptorCommands
{

    public PageDescriptorCommands page()
    {
        return new PageDescriptorCommands();
    }

    public PartDescriptorCommands part()
    {
        return new PartDescriptorCommands();
    }

    public LayoutDescriptorCommands layout()
    {
        return new LayoutDescriptorCommands();
    }
}
