package com.enonic.wem.api.content.page.part;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.api.module.ModuleName;

import static com.google.common.base.Splitter.on;
import static com.google.common.collect.Iterators.toArray;

public class PartTemplateKey
    extends TemplateKey<PartTemplateName>
{
    private PartTemplateKey( final ModuleName moduleName, final PartTemplateName templateName )
    {
        super( moduleName, templateName, TemplateType.PART );
    }

    public static PartTemplateKey from( final ModuleName moduleName, final PartTemplateName templateName )
    {
        return new PartTemplateKey( moduleName, templateName );
    }

    public static PartTemplateKey from( final String templateKey )
    {
        final String[] templateKeyParts = toArray( on( SEPARATOR ).split( templateKey ).iterator(), String.class );
        Preconditions.checkArgument( templateKeyParts.length == 2, "Invalid PartTemplateKey" );

        final ModuleName moduleName = ModuleName.from( templateKeyParts[0] );
        final PartTemplateName pageTemplateName = new PartTemplateName( templateKeyParts[1] );

        return new PartTemplateKey( moduleName, pageTemplateName );
    }
}
