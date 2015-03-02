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
import static org.junit.Assert.assertNull;

public class ContentServiceImplTest_getNearestSite extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void child_of_site()
        throws Exception
    {
        final Content site = createSite();

        final Content child = createContent(site.getPath());

        final Site fetchedSite = this.contentService.getNearestSite( child.getId() );

        assertNotNull( fetchedSite );
        assertEquals(site.getId(), fetchedSite.getId());

    }

    @Test
    public void is_site()
        throws Exception
    {
        final Content site = createSite();

        final Site fetchedSite = this.contentService.getNearestSite( site.getId() );

        assertNotNull( fetchedSite );
        assertEquals(site.getId(), fetchedSite.getId());

    }

    @Test
    public void no_site_in_path()
        throws Exception
    {
        final Content content = createContent(ContentPath.ROOT);

        final Site fetchedSite = this.contentService.getNearestSite( content.getId() );
        assertNull( fetchedSite );
    }

    @Test
    public void deep_child_of_site()
        throws Exception
    {
        final Content site = createSite();

        final Content childLevel1 = createContent(site.getPath());
        final Content childLevel2 = createContent(childLevel1.getPath());
        final Content childLevel3 = createContent(childLevel2.getPath());

        final Site fetchedSite = this.contentService.getNearestSite( childLevel3.getId() );

        assertNotNull( fetchedSite );
        assertEquals( site.getId(), fetchedSite.getId() );
    }

    private Content createSite()
    throws Exception
    {
            final CreateSiteParams createSiteParams = new CreateSiteParams();
            createSiteParams.parent( ContentPath.ROOT ).
            displayName( "My mock site" ).
            description( "This is my mock site" ).
            moduleConfigs( ModuleConfigs.empty() );

        return this.contentService.create( createSiteParams );
    }

    private Content createContent(ContentPath parentPath)
        throws Exception {

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( parentPath ).
            type( ContentTypeName.folder() ).
            build();

        return this.contentService.create( createContentParams );
    }
}
