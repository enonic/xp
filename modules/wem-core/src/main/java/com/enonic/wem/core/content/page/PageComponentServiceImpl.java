package com.enonic.wem.core.content.page;

import javax.inject.Inject;

import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageComponentService;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorService;
import com.enonic.wem.api.content.page.part.PartDescriptorService;
import com.enonic.wem.api.module.ModuleKey;

public final class PageComponentServiceImpl
    implements PageComponentService
{
    @Inject
    protected PartDescriptorService partDescriptorService;

    @Inject
    protected LayoutDescriptorService layoutDescriptorService;

    @Override
    public PageComponent getByName( final ModuleKey module, final ComponentName name )
    {
        return new GetPageComponentByNameCommand().
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            module( module ).
            name( name ).
            execute();
    }

    public void setPartDescriptorService( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
    }

    public void setLayoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
    }
}
