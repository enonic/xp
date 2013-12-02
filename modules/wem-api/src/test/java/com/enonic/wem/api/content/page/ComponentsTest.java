package com.enonic.wem.api.content.page;

import org.junit.Test;

import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.layout.LayoutTemplateName;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.part.PartTemplateName;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;

import static org.junit.Assert.*;

public class ComponentsTest
{

    @Test
    public void page()
    {
        RootDataSet pageConfig = new RootDataSet();
        pageConfig.addProperty( "pause", new Value.Long( 200 ) );

        Page page = Page.newPage().
            template( new PageTemplateName( "pageTemplateName" ) ).
            config( pageConfig ).
            build();

        assertEquals( "pageTemplateName", page.getTemplate().toString() );
    }

    @Test
    public void part()
    {
        RootDataSet partConfig = new RootDataSet();
        partConfig.addProperty( "width", new Value.Long( 150 ) );

        PartComponent partComponent = PartComponent.newPart().
            partTemplateName( new PartTemplateName( "partTemplateName" ) ).
            config( partConfig ).
            build();

        assertEquals( "partTemplateName", partComponent.getTemplate().toString() );
    }

    @Test
    public void layout()
    {
        RootDataSet layoutConfig = new RootDataSet();
        layoutConfig.addProperty( "columns", new Value.Long( 2 ) );

        LayoutComponent layoutComponent = LayoutComponent.newLayout().
            layoutTemplateName( new LayoutTemplateName( "layoutTemplateName" ) ).
            config( layoutConfig ).
            build();

        assertEquals( "layoutTemplateName", layoutComponent.getTemplate().toString() );
    }
}
