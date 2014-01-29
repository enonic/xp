package com.enonic.wem.api.content.page.layout;


import com.enonic.wem.api.content.page.TemplateName;

public class LayoutTemplateName
    extends TemplateName
{
    public LayoutTemplateName( final String name )
    {
        super( name );
    }

    public static LayoutTemplateName from( final String name )
    {
        return new LayoutTemplateName( name );
    }
}
