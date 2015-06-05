package com.enonic.wem.core.site;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.site.CreateSiteParams;
import com.enonic.xp.content.site.ModuleConfigs;

import static org.junit.Assert.*;

public class SiteServiceImplTest_create
    extends AbstractSiteServiceTest
{
    @Test
    public void create_site()
        throws Exception
    {
        final CreateSiteParams createSiteParams = new CreateSiteParams();
        createSiteParams.parent( ContentPath.ROOT ).
            displayName( "My site" ).
            description( "This is my site" ).
            moduleConfigs( ModuleConfigs.empty() );

        final Content content = this.siteService.create( createSiteParams );

        assertNotNull( content.getName() );
        assertNotNull( content.getCreatedTime() );
        assertNotNull( content.getCreator() );
        assertNotNull( content.getModifiedTime() );
        assertNotNull( content.getModifier() );
    }
}
