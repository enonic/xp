package com.enonic.wem.api.content.page.image;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.module.ModuleKey;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.Iterators.toArray;

public class ImageTemplateKey
    extends TemplateKey<ImageTemplateName>
{
    private ImageTemplateKey( final SiteTemplateKey siteTemplateKey, final ModuleKey moduleKey, final ImageTemplateName templateName )
    {
        super( siteTemplateKey, moduleKey, templateName );
    }

    public static ImageTemplateKey from( final SiteTemplateKey siteTemplateKey, final ModuleKey moduleKey,
                                         final ImageTemplateName templateName )
    {
        return new ImageTemplateKey( siteTemplateKey, moduleKey, templateName );
    }

    public static ImageTemplateKey from( final String templateKey )
    {
        final String[] templateKeyParts = toArray( on( SEPARATOR ).split( templateKey ).iterator(), String.class );
        Preconditions.checkArgument( templateKeyParts.length != 3, "Invalid ImageTemplateKey" );

        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( templateKeyParts[0] );
        final ModuleKey moduleKey = ModuleKey.from( templateKeyParts[1] );
        final ImageTemplateName pageTemplateName = new ImageTemplateName( templateKeyParts[2] );

        return new ImageTemplateKey( siteTemplateKey, moduleKey, pageTemplateName );
    }
}
