package com.enonic.wem.api.content.page;

import org.junit.Test;

import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.part.PartTemplateKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;

import static com.enonic.wem.api.content.page.Page.newPage;
import static com.enonic.wem.api.content.page.layout.LayoutComponent.newLayoutComponent;
import static com.enonic.wem.api.content.page.part.PartComponent.newPartComponent;
import static org.junit.Assert.*;

public class ComponentsTest
{

    @Test
    public void page()
    {
        RootDataSet pageConfig = new RootDataSet();
        pageConfig.addProperty( "pause", new Value.Long( 200 ) );

        Page page = newPage().
            template( PageTemplateKey.from( "mainmodule|pageTemplateName" ) ).
            config( pageConfig ).
            regions( PageRegions.newPageRegions().build() ).
            build();

        assertEquals( "pageTemplateName", page.getTemplate().getTemplateName().toString() );
        assertEquals( "mainmodule", page.getTemplate().getModuleName().toString() );
    }

    @Test
    public void part()
    {
        RootDataSet partConfig = new RootDataSet();
        partConfig.addProperty( "width", new Value.Long( 150 ) );

        PartComponent partComponent = newPartComponent().
            name( "my-part" ).
            template( PartTemplateKey.from( "mainmodule|partTemplateName" ) ).
            config( partConfig ).
            build();

        assertEquals( "my-part", partComponent.getName().toString() );
        assertEquals( "partTemplateName", partComponent.getTemplate().getTemplateName().toString() );
        assertEquals( "mainmodule", partComponent.getTemplate().getModuleName().toString() );
    }

    @Test
    public void layout()
    {
        RootDataSet layoutConfig = new RootDataSet();
        layoutConfig.addProperty( "columns", new Value.Long( 2 ) );

        LayoutComponent layoutComponent = newLayoutComponent().
            name( "my-template" ).
            template( LayoutTemplateKey.from( "mainmodule|layoutTemplateName" ) ).
            config( layoutConfig ).
            build();

        assertEquals( "my-template", layoutComponent.getName().toString() );
        assertEquals( "layoutTemplateName", layoutComponent.getTemplate().getTemplateName().toString() );
        assertEquals( "mainmodule", layoutComponent.getTemplate().getModuleName().toString() );
    }
}
