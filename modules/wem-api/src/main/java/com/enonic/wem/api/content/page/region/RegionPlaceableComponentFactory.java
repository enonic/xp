package com.enonic.wem.api.content.page.region;


import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.data.DataSet;

import static com.enonic.wem.api.content.page.image.ImageComponent.newImageComponent;
import static com.enonic.wem.api.content.page.layout.LayoutComponent.newLayoutComponent;
import static com.enonic.wem.api.content.page.part.PartComponent.newPartComponent;

class RegionPlaceableComponentFactory
{
    static PageComponent create( final DataSet dataSet )
    {
        final String classSimpleName = dataSet.getName();
        if ( LayoutComponent.class.getSimpleName().equals( classSimpleName ) )
        {
            return newLayoutComponent().from( dataSet ).build();
        }
        else if ( ImageComponent.class.getSimpleName().equals( classSimpleName ) )
        {
            return newImageComponent().from( dataSet ).build();
        }
        else if ( PartComponent.class.getSimpleName().equals( classSimpleName ) )
        {
            return newPartComponent().from( dataSet ).build();
        }
        else
        {
            throw new IllegalArgumentException( "Not a component that can be placed in a Region: " + classSimpleName );
        }
    }
}
