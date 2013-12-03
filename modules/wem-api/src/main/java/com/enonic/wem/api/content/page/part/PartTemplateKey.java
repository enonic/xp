package com.enonic.wem.api.content.page.part;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.module.ModuleKey;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.Iterators.toArray;

public class PartTemplateKey
    extends TemplateKey<PartTemplateName>
{
    private PartTemplateKey( final SiteTemplateKey siteTemplateKey, final ModuleKey moduleKey, final PartTemplateName templateName )
    {
        super( siteTemplateKey, moduleKey, templateName );
    }

    public static PartTemplateKey from( final SiteTemplateKey siteTemplateKey, final ModuleKey moduleKey,
                                        final PartTemplateName templateName )
    {
        return new PartTemplateKey( siteTemplateKey, moduleKey, templateName );
    }

    public static PartTemplateKey from( final String templateKey )
    {
        final String[] templateKeyParts = toArray( on( SEPARATOR ).split( templateKey ).iterator(), String.class );
        Preconditions.checkArgument( templateKeyParts.length != 3, "Invalid PartTemplateKey" );

        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( templateKeyParts[0] );
        final ModuleKey moduleKey = ModuleKey.from( templateKeyParts[1] );
        final PartTemplateName pageTemplateName = new PartTemplateName( templateKeyParts[2] );

        return new PartTemplateKey( siteTemplateKey, moduleKey, pageTemplateName );
    }
}
