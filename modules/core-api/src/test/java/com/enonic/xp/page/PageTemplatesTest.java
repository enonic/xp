package com.enonic.xp.page;

import java.util.Arrays;

import org.junit.Test;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;

import static org.junit.Assert.*;

public class PageTemplatesTest
{
    @Test
    public void empty()
    {
        assertTrue( PageTemplates.empty().isEmpty() );
    }

    @Test
    public void getTemplate()
    {
        final PageTemplates pageTemplates = PageTemplates.from( PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "testKey" ) ).
            name( "testContentName" ).
            parentPath( ContentPath.from( "path" ) ).
            build() );

        assertNotNull( pageTemplates.getTemplate( PageTemplateKey.from( ContentId.from( "testKey" ) ) ) );
        assertNull( pageTemplates.getTemplate( PageTemplateKey.from( ContentId.from( "nonExistingKey" ) ) ) );
        assertNotNull( pageTemplates.getTemplate( ContentName.from( "testContentName" ) ) );
        assertNull( pageTemplates.getTemplate( ContentName.from( "nonExistingContentName" ) ) );
    }

    @Test
    public void filterTest()
    {
        final PageTemplates pageTemplates = PageTemplates.newPageTemplates().
            add( generatePageTemplate1() ).
            addAll( Arrays.asList( generatePageTemplate2(), generatePageTemplate3() ) ).
            build();

        final PageTemplateSpec pageTemplateSpec = PageTemplateSpec.newPageTemplateParams().
            canRender( ContentTypeName.imageMedia() ).
            build();

        final PageTemplates filteredPageTemplates = pageTemplates.filter( pageTemplateSpec );

        assertNotNull( filteredPageTemplates );
        assertEquals( 2, filteredPageTemplates.getSize() );
    }

    private PageTemplate generatePageTemplate1()
    {
        return PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "testKey" ) ).
            canRender( ContentTypeNames.from( ContentTypeName.archiveMedia(), ContentTypeName.imageMedia() ) ).
            regions( PageRegions.newPageRegions().build() ).
            name( "testContentName" ).
            id( ContentId.from( "id" ) ).
            parentPath( ContentPath.from( "path" ) ).
            build();
    }

    private PageTemplate generatePageTemplate2()
    {
        return PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "testKey2" ) ).
            canRender( ContentTypeNames.from( ContentTypeName.dataMedia() ) ).
            controller( DescriptorKey.from( "descriptor2" ) ).
            name( "testContentName2" ).
            parentPath( ContentPath.from( "path2" ) ).
            build();
    }

    private PageTemplate generatePageTemplate3()
    {
        return PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "testKey3" ) ).
            canRender( ContentTypeNames.from( ContentTypeName.audioMedia(), ContentTypeName.imageMedia() ) ).
            config( new PropertyTree() ).
            name( "testContentName3" ).
            parentPath( ContentPath.from( "path3" ) ).
            build();
    }
}
