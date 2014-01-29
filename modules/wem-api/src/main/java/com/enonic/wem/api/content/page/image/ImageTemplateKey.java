package com.enonic.wem.api.content.page.image;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.api.module.ModuleName;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.Iterators.toArray;

public class ImageTemplateKey
    extends TemplateKey<ImageTemplateName>
{
    private ImageTemplateKey( final ModuleName moduleKey, final ImageTemplateName templateName )
    {
        super( moduleKey, templateName, TemplateType.IMAGE );
    }

    public static ImageTemplateKey from( final ModuleName moduleKey, final ImageTemplateName templateName )
    {
        return new ImageTemplateKey( moduleKey, templateName );
    }

    public static ImageTemplateKey from( final String templateKey )
    {
        final String[] templateKeyParts = toArray( on( SEPARATOR ).split( templateKey ).iterator(), String.class );
        Preconditions.checkArgument( templateKeyParts.length == 2, "Invalid ImageTemplateKey" );

        final ModuleName moduleName = ModuleName.from( templateKeyParts[0] );
        final ImageTemplateName templateName = new ImageTemplateName( templateKeyParts[1] );

        return new ImageTemplateKey( moduleName, templateName );
    }
}
