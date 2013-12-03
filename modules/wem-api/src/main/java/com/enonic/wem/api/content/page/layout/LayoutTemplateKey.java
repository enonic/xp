package com.enonic.wem.api.content.page.layout;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.module.ModuleKey;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.Iterators.toArray;

public class LayoutTemplateKey
    extends TemplateKey<LayoutTemplateName>
{
    private LayoutTemplateKey( final SiteTemplateKey siteTemplateKey, final ModuleKey moduleKey, final LayoutTemplateName templateName )
    {
        super( siteTemplateKey, moduleKey, templateName );
    }

    public static LayoutTemplateKey from( final SiteTemplateKey siteTemplateKey, final ModuleKey moduleKey,
                                          final LayoutTemplateName templateName )
    {
        return new LayoutTemplateKey( siteTemplateKey, moduleKey, templateName );
    }

    public static LayoutTemplateKey from( final String templateKey )
    {
        final String[] templateKeyParts = toArray( on( SEPARATOR ).split( templateKey ).iterator(), String.class );
        Preconditions.checkArgument( templateKeyParts.length != 3, "Invalid LayoutTemplateKey" );

        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( templateKeyParts[0] );
        final ModuleKey moduleKey = ModuleKey.from( templateKeyParts[1] );
        final LayoutTemplateName pageTemplateName = new LayoutTemplateName( templateKeyParts[2] );

        return new LayoutTemplateKey( siteTemplateKey, moduleKey, pageTemplateName );
    }
}
