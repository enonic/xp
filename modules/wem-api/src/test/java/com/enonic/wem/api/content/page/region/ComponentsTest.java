package com.enonic.wem.api.content.page.region;

import org.junit.Test;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.data.PropertyTree;

import static com.enonic.wem.api.content.page.Page.newPage;
import static com.enonic.wem.api.content.page.region.LayoutComponent.newLayoutComponent;
import static com.enonic.wem.api.content.page.region.PartComponent.newPartComponent;
import static org.junit.Assert.*;

public class ComponentsTest
{

    @Test
    public void page()
    {
        PropertyTree pageConfig = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        pageConfig.addLong( "pause", 200L );

        Page page = newPage().
            template( PageTemplateKey.from( "pageTemplateName" ) ).
            config( pageConfig ).
            regions( PageRegions.newPageRegions().build() ).
            build();

        assertEquals( "pageTemplateName", page.getTemplate().toString() );
    }

    @Test
    public void part()
    {
        PropertyTree partConfig = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        partConfig.addLong( "width", 150L );

        PartComponent partComponent = newPartComponent().
            name( "my-part" ).
            descriptor( DescriptorKey.from( "mainmodule:partTemplateName" ) ).
            config( partConfig ).
            build();

        assertEquals( "my-part", partComponent.getName().toString() );
        assertEquals( "partTemplateName", partComponent.getDescriptor().getName().toString() );
        assertEquals( "mainmodule", partComponent.getDescriptor().getModuleKey().toString() );
    }

    @Test
    public void layout()
    {
        PropertyTree layoutConfig = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        layoutConfig.addLong( "columns", 2L );

        LayoutComponent layoutComponent = newLayoutComponent().
            name( "my-template" ).
            descriptor( DescriptorKey.from( "mainmodule:layoutTemplateName" ) ).
            config( layoutConfig ).
            build();

        assertEquals( "my-template", layoutComponent.getName().toString() );
        assertEquals( "layoutTemplateName", layoutComponent.getDescriptor().getName().toString() );
        assertEquals( "mainmodule", layoutComponent.getDescriptor().getModuleKey().toString() );
    }
}
