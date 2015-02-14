package com.enonic.xp.content.page;

import org.junit.Test;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.page.DescriptorKey;
import com.enonic.xp.content.page.PageRegions;
import com.enonic.xp.content.page.PageTemplate;
import com.enonic.xp.content.page.PageTemplateKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;

import static org.junit.Assert.*;

public class PageTemplateTest
{
    @Test
    public void pageTemplate()
    {
        PropertyTree pageTemplateConfig = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        pageTemplateConfig.addLong( "pause", 10000L );

        final PageTemplate.Builder builder = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "abcdefg" ) ).
            canRender( ContentTypeNames.from( "mainmodule:article", "mainmodule:banner" ) ).
            controller( DescriptorKey.from( ModuleKey.from( "mainmodule" ), "landing-page" ) ).
            config( pageTemplateConfig ).
            regions( PageRegions.newPageRegions().build() );
        builder.displayName( "Main page template" );
        builder.name( "main-page-template" );
        builder.parentPath( ContentPath.ROOT );
        PageTemplate pageTemplate = builder.build();

        assertEquals( "main-page-template", pageTemplate.getName().toString() );
        assertEquals( "Main page template", pageTemplate.getDisplayName() );
        assertTrue( pageTemplate.getCanRender().contains( ContentTypeName.from( "mainmodule:article" ) ) );
    }
}
