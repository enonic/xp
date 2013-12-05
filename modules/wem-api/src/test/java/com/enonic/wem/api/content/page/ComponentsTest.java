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
            template( PageTemplateKey.from( "sitetemplate-1.0.0|mainmodule-1.0.0|pageTemplateName" ) ).
            config( pageConfig ).
            build();

        assertEquals( "pageTemplateName", page.getTemplate().getTemplateName().toString() );
        assertEquals( "mainmodule-1.0.0", page.getTemplate().getModuleKey().toString() );
        assertEquals( "sitetemplate-1.0.0", page.getTemplate().getSiteTemplateKey().toString() );
    }

    @Test
    public void part()
    {
        RootDataSet partConfig = new RootDataSet();
        partConfig.addProperty( "width", new Value.Long( 150 ) );

        PartComponent partComponent = newPartComponent().
            template( PartTemplateKey.from( "sitetemplate-1.0.0|mainmodule-1.0.0|partTemplateName" ) ).
            config( partConfig ).
            build();

        assertEquals( "partTemplateName", partComponent.getTemplate().getTemplateName().toString() );
        assertEquals( "mainmodule-1.0.0", partComponent.getTemplate().getModuleKey().toString() );
        assertEquals( "sitetemplate-1.0.0", partComponent.getTemplate().getSiteTemplateKey().toString() );
    }

    @Test
    public void layout()
    {
        RootDataSet layoutConfig = new RootDataSet();
        layoutConfig.addProperty( "columns", new Value.Long( 2 ) );

        LayoutComponent layoutComponent = newLayoutComponent().
            template( LayoutTemplateKey.from( "sitetemplate-1.0.0|mainmodule-1.0.0|layoutTemplateName" ) ).
            config( layoutConfig ).
            build();

        assertEquals( "layoutTemplateName", layoutComponent.getTemplate().getTemplateName().toString() );
        assertEquals( "mainmodule-1.0.0", layoutComponent.getTemplate().getModuleKey().toString() );
        assertEquals( "sitetemplate-1.0.0", layoutComponent.getTemplate().getSiteTemplateKey().toString() );
    }
}
