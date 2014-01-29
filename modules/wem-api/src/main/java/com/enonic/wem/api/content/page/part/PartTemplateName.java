package com.enonic.wem.api.content.page.part;


import com.enonic.wem.api.content.page.TemplateName;

public class PartTemplateName
    extends TemplateName
{
    public PartTemplateName( final String name )
    {
        super( name );
    }

    public static PartTemplateName from( final String name )
    {
        return new PartTemplateName( name );
    }
}
