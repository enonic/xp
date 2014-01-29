package com.enonic.wem.api.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.module.ModuleName;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.Iterators.toArray;

public final class PageTemplateKey
    extends TemplateKey<PageTemplateName>
{
    private PageTemplateKey( final ModuleName moduleName, final PageTemplateName templateName )
    {
        super( moduleName, templateName, TemplateType.PAGE );
    }

    public static PageTemplateKey from( final ModuleName moduleKey, final PageTemplateName templateName )
    {
        return new PageTemplateKey( moduleKey, templateName );
    }

    public static PageTemplateKey from( final String templateKey )
    {
        Preconditions.checkNotNull( templateKey, "templateKey cannot be null" );
        final String[] templateKeyParts = toArray( on( SEPARATOR ).split( templateKey ).iterator(), String.class );
        Preconditions.checkArgument( templateKeyParts.length == 2, "Invalid PageTemplateKey" );

        final ModuleName moduleName = ModuleName.from( templateKeyParts[0] );
        final PageTemplateName templateName = new PageTemplateName( templateKeyParts[1] );

        return new PageTemplateKey( moduleName, templateName );
    }
}
