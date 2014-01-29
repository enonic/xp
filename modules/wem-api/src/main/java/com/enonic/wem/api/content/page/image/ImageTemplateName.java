package com.enonic.wem.api.content.page.image;


import com.enonic.wem.api.content.page.TemplateName;

public class ImageTemplateName
    extends TemplateName
{
    public ImageTemplateName( final String name )
    {
        super( name );
    }

    public static ImageTemplateName from( final String name )
    {
        return new ImageTemplateName( name );
    }
}
