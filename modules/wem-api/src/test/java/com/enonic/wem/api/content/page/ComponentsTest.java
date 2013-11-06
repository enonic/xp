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
            pageTemplateId( new PageTemplateId( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            config( pageConfig ).
            build();

        assertEquals( "1fad493a-6a72-41a3-bac4-88aba3d83bcc", page.getTemplateId().toString() );
    }

    @Test
    public void part()
    {
        RootDataSet partConfig = new RootDataSet();
        partConfig.addProperty( "width", new Value.Long( 150 ) );

        Part part = Part.newPart().
            partTemplateId( new PartTemplateId( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            config( partConfig ).
            build();

        assertEquals( "1fad493a-6a72-41a3-bac4-88aba3d83bcc", part.getTemplateId().toString() );
    }

    @Test
    public void layout()
    {
        RootDataSet layoutConfig = new RootDataSet();
        layoutConfig.addProperty( "columns", new Value.Long( 2 ) );

        Layout layout = Layout.newLayout().
            layoutTemplateId( new LayoutTemplateId( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            config( layoutConfig ).
            build();

        assertEquals( "1fad493a-6a72-41a3-bac4-88aba3d83bcc", layout.getTemplateId().toString() );
    }
}
