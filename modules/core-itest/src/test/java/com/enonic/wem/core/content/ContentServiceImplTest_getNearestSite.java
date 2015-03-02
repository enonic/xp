package com.enonic.wem.core.content;

import org.junit.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;

import com.enonic.xp.content.site.CreateSiteParams;
import com.enonic.xp.content.site.ModuleConfigs;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.content.site.Site;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ContentServiceImplTest_getNearestSite extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void getNearestSite()
        throws Exception
    {

        final Content site = create_site();

        final Content content = create_content(site.getPath());

        final Site fetchedSite1 = this.contentService.getNearestSite( content.getId() );
        final Site fetchedSite2 = this.contentService.getNearestSite( site.getId() );

        assertNotNull( fetchedSite1 );
        assertEquals(site.getId(), fetchedSite1.getId());
        assertNotNull( fetchedSite2 );
        assertEquals(site.getId(), fetchedSite2.getId());

    }

    public Content create_site()
    throws Exception
    {

            final CreateSiteParams createSiteParams = new CreateSiteParams();
            createSiteParams.parent( ContentPath.ROOT ).
            displayName( "My mock site" ).
            description( "This is my mock site" ).
            moduleConfigs( ModuleConfigs.empty() );

        return this.contentService.create( createSiteParams );
    }

    public Content create_content(ContentPath parentPath)
        throws Exception {

        final String name = "cat-small.jpg";
        final ByteSource image = loadImage( name );

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( parentPath ).
            type( ContentTypeName.imageMedia() ).
            createAttachments( createAttachment( "cat", "image/jpeg", image ) ).
            build();

        return this.contentService.create( createContentParams );
    }
}
