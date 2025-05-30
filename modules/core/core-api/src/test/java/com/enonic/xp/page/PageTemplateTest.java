package com.enonic.xp.page;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PageTemplateTest
{
    @Test
    public void pageTemplate()
    {
        final PropertyTree pageTemplateConfig = new PropertyTree();
        pageTemplateConfig.addLong( "pause", 10000L );

        final PageTemplate.Builder builder = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "abcdefg" ) ).
            canRender( ContentTypeNames.from( "mainmodule:article", "mainmodule:banner" ) ).
            controller( DescriptorKey.from( ApplicationKey.from( "mainmodule" ), "landing-page" ) ).
            config( pageTemplateConfig ).
            regions( PageRegions.create().build() );
        builder.displayName( "Main page template" );
        builder.name( "main-page-template" );
        builder.parentPath( ContentPath.ROOT );
        PageTemplate pageTemplate = builder.build();

        assertEquals( "main-page-template", pageTemplate.getName().toString() );
        assertEquals( "Main page template", pageTemplate.getDisplayName() );
        assertTrue( pageTemplate.getCanRender().contains( ContentTypeName.from( "mainmodule:article" ) ) );
        assertTrue( pageTemplate.canRender( ContentTypeName.from( "mainmodule:article" ) ) );
        assertTrue( pageTemplate.canRender( ContentTypeName.from( "mainmodule:banner" ) ) );
        assertEquals( DescriptorKey.from( ApplicationKey.from( "mainmodule" ), "landing-page" ), pageTemplate.getController() );
        assertEquals( pageTemplateConfig, pageTemplate.getConfig() );
        assertTrue( pageTemplate.hasRegions() );
    }
}
