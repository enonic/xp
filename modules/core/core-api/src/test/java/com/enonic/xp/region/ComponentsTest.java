package com.enonic.xp.region;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.page.PageTemplateKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComponentsTest
{
    @Test
    public void page()
    {
        PropertyTree pageConfig = new PropertyTree();
        pageConfig.addLong( "pause", 200L );

        Page page = Page.create().
            template( PageTemplateKey.from( "pageTemplateName" ) ).
            config( pageConfig ).
            regions( PageRegions.create().build() ).
            build();

        assertEquals( "pageTemplateName", page.getTemplate().toString() );
    }

    @Test
    public void part()
    {
        PropertyTree partConfig = new PropertyTree();
        partConfig.addLong( "width", 150L );

        PartComponent partComponent = PartComponent.create().
            descriptor( DescriptorKey.from( "mainmodule:partTemplateName" ) ).
            config( partConfig ).
            build();

        assertEquals( "Part", partComponent.getName().toString() );
        assertEquals( "partTemplateName", partComponent.getDescriptor().getName() );
        assertEquals( "mainmodule", partComponent.getDescriptor().getApplicationKey().toString() );
        assertEquals( PartComponentType.INSTANCE, partComponent.getType() );
    }

    @Test
    public void layout()
    {
        PropertyTree layoutConfig = new PropertyTree();
        layoutConfig.addLong( "columns", 2L );

        LayoutComponent layoutComponent = LayoutComponent.create().
            descriptor( DescriptorKey.from( "mainmodule:layoutTemplateName" ) ).
            config( layoutConfig ).
            build();

        assertEquals( "Fragment", layoutComponent.getName().toString() );
        assertEquals( "layoutTemplateName", layoutComponent.getDescriptor().getName() );
        assertEquals( "mainmodule", layoutComponent.getDescriptor().getApplicationKey().toString() );
    }
}
