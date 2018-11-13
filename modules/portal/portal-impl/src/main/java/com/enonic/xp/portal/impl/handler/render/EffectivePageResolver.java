package com.enonic.xp.portal.impl.handler.render;

import com.enonic.xp.content.Content;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplate;

final class EffectivePageResolver
{
    private final Content content;

    private final PageTemplate template;

    public EffectivePageResolver( final Content content, final PageTemplate template )
    {
        this.content = content;
        this.template = template;
    }

    public Page resolve()
    {
        if ( content instanceof PageTemplate )
        {
            return content.getPage();
        }
        else if ( template == null )
        {
            return content.getPage();
        }
        else if ( !content.hasPage() )
        {
            // The Content has no Page, but it has a supporting PageTemplate, so then we use the Page from the PageTemplate instead
            if ( template.getPage() == null )
            {
                return Page.create().
                    template( template.getKey() ).
                    build();
            }
            else
            {
                return Page.create( template.getPage() ).
                    descriptor( null ).
                    template( template.getKey() ).
                    build();
            }
        }
        else
        {
            final Page contentPage = content.getPage();
            final Page.Builder effectivePage = Page.create( template.getPage() ).
                descriptor( null ).
                template( contentPage.getTemplate() );

            if ( contentPage.hasDescriptor() )
            {
                effectivePage.descriptor( contentPage.getDescriptor() );
                effectivePage.template( null );
            }
            if ( contentPage.hasConfig() )
            {
                effectivePage.config( contentPage.getConfig() );
            }
            if ( contentPage.hasRegions() )
            {
                effectivePage.regions( contentPage.getRegions() );
            }
            return effectivePage.build();
        }
    }
}
