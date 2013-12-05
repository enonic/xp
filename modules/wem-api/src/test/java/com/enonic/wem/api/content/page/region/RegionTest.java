package com.enonic.wem.api.content.page.region;


import org.junit.Test;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.image.ImageTemplateKey;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;

import static com.enonic.wem.api.content.page.image.ImageComponent.newImageComponent;
import static com.enonic.wem.api.content.page.region.Region.newRegion;
import static junit.framework.Assert.assertEquals;

public class RegionTest
{
    @Test
    public void serialization_to_RootDataSet()
    {
        RootDataSet imageComponentConfig = new RootDataSet();
        imageComponentConfig.setProperty( "width", new Value.Long( 300 ) );
        imageComponentConfig.setProperty( "caption", new Value.String( "Pass photo" ) );

        Region region = newRegion().
            name( "myRegion" ).
            add( newImageComponent().
                image( ContentId.from( "123" ) ).
                template( ImageTemplateKey.from( "mysitetemplate-1.0.0|mymodule-1.0.0|mypagetemplate" ) ).
                config( imageComponentConfig ).
                build() ).
            build();

        RootDataSet rootDataSet = region.toData();
        assertEquals( "myRegion", rootDataSet.getProperty( "name" ).getString() );

        DataSet imageComponentAsDataSet = rootDataSet.getDataSet( "ImageComponent" );
        assertEquals( "123", imageComponentAsDataSet.getProperty( "image" ).getContentId().toString() );
        assertEquals( "mysitetemplate-1.0.0|mymodule-1.0.0|mypagetemplate", imageComponentAsDataSet.getProperty( "template" ).getString() );

        RootDataSet configRootDataSet = imageComponentAsDataSet.getProperty( "config" ).getData();
        assertEquals( new Long( 300 ), configRootDataSet.getProperty( "width" ).getLong() );
        assertEquals( "Pass photo", configRootDataSet.getProperty( "caption" ).getString() );
    }
}
