package com.enonic.xp.core.impl.content.page.region;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.region.CreateFragmentParams;
import com.enonic.xp.region.FragmentService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;

@Component(immediate = true)
public final class FragmentServiceImpl
    implements FragmentService
{
    private ContentService contentService;

    private PartDescriptorService partDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    @Override
    public Content create( final CreateFragmentParams params )
    {
        return CreateFragmentCommand.create().
            contentService( this.contentService ).
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            params( params ).
            build().
            execute();
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setLayoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
    }

    @Reference
    public void setPartDescriptorService( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
    }
}
