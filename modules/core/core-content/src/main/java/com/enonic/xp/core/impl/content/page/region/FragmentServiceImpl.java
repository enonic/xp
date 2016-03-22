package com.enonic.xp.core.impl.content.page.region;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.region.CreateFragmentParams;
import com.enonic.xp.region.FragmentService;

@Component(immediate = true)
public final class FragmentServiceImpl
    implements FragmentService
{
    private ContentService contentService;

    @Override
    public Content create( final CreateFragmentParams params )
    {
        return CreateFragmentCommand.create().
            contentService( this.contentService ).
            params( params ).
            build().
            execute();
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
