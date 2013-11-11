package com.enonic.wem.api.content.page;

import org.junit.Test;

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
            pageTemplateName( new PageTemplateName( "pageTemplateName" ) ).
            config( pageConfig ).
            build();

        assertEquals( "pageTemplateName", page.getTemplateName().toString() );
    }

    @Test
    public void part()
    {
        RootDataSet partConfig = new RootDataSet();
        partConfig.addProperty( "width", new Value.Long( 150 ) );

        Part part = Part.newPart().
            partTemplateName( new PartTemplateName( "partTemplateName" ) ).
            config( partConfig ).
            build();

        assertEquals( "partTemplateName", part.getTemplateName().toString() );
    }

    @Test
    public void layout()
    {
        RootDataSet layoutConfig = new RootDataSet();
        layoutConfig.addProperty( "columns", new Value.Long( 2 ) );

        Layout layout = Layout.newLayout().
            layoutTemplateName( new LayoutTemplateName( "layoutTemplateName" ) ).
            config( layoutConfig ).
            build();

        assertEquals( "layoutTemplateName", layout.getTemplateName().toString() );
    }
}
