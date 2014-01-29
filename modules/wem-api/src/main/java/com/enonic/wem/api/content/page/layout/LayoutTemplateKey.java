package com.enonic.wem.api.content.page.layout;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.api.module.ModuleName;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.Iterators.toArray;

public class LayoutTemplateKey
    extends TemplateKey<LayoutTemplateName>
{
    private LayoutTemplateKey( final ModuleName moduleName, final LayoutTemplateName templateName )
    {
        super( moduleName, templateName, TemplateType.LAYOUT );
    }

    public static LayoutTemplateKey from( final ModuleName moduleName, final LayoutTemplateName templateName )
    {
        return new LayoutTemplateKey( moduleName, templateName );
    }

    public static LayoutTemplateKey from( final String templateKey )
    {
        final String[] templateKeyParts = toArray( on( SEPARATOR ).split( templateKey ).iterator(), String.class );
        Preconditions.checkArgument( templateKeyParts.length == 2, "Invalid LayoutTemplateKey" );

        final ModuleName moduleName = ModuleName.from( templateKeyParts[0] );
        final LayoutTemplateName pageTemplateName = new LayoutTemplateName( templateKeyParts[1] );

        return new LayoutTemplateKey( moduleName, pageTemplateName );
    }
}
