package com.enonic.wem.core.site;

import org.junit.Before;

import com.enonic.wem.core.content.AbstractContentServiceTest;
import com.enonic.xp.core.impl.site.SiteServiceImpl;

public class AbstractSiteServiceTest
    extends AbstractContentServiceTest
{

    protected SiteServiceImpl siteService;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        siteService = new SiteServiceImpl();
        siteService.setContentService( this.contentService );
    }
}
