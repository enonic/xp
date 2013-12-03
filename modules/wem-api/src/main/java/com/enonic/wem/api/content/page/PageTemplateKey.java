package com.enonic.wem.api.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.module.ModuleKey;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.Iterators.toArray;

public final class PageTemplateKey
    extends TemplateKey<PageTemplateName>
{
    private PageTemplateKey( final SiteTemplateKey siteTemplateKey, final ModuleKey moduleKey, final PageTemplateName templateName )
    {
        super( siteTemplateKey, moduleKey, templateName );
    }

    public static PageTemplateKey from( final SiteTemplateKey siteTemplateKey, final ModuleKey moduleKey,
                                        final PageTemplateName templateName )
    {
        return new PageTemplateKey( siteTemplateKey, moduleKey, templateName );
    }

    public static PageTemplateKey from( final String templateKey )
    {
        final String[] templateKeyParts = toArray( on( SEPARATOR ).split( templateKey ).iterator(), String.class );
        Preconditions.checkArgument( templateKeyParts.length != 3, "Invalid PageTemplateKey" );

        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( templateKeyParts[0] );
        final ModuleKey moduleKey = ModuleKey.from( templateKeyParts[1] );
        final PageTemplateName pageTemplateName = new PageTemplateName( templateKeyParts[2] );

        return new PageTemplateKey( siteTemplateKey, moduleKey, pageTemplateName );
    }
}
