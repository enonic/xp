package com.enonic.xp.lib.portal.current;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.Site;
import com.enonic.xp.testing.ScriptTestSupport;

public class GetCurrentSiteConfigScriptTest
    extends ScriptTestSupport
{
    @Test
    public void currentSite()
    {
        final Site site = TestDataFixtures.newSite().build();
        this.portalRequest.setSite( site );

        runFunction( "/test/getCurrentSiteConfig-test.js", "currentSite" );
    }

    @Test
    public void noCurrentSite()
    {
        this.portalRequest.setSite( null );
        runFunction( "/test/getCurrentSiteConfig-test.js", "noCurrentSite" );
    }

    @Test
    public void noCurrentApplication()
    {
        this.portalRequest.setApplicationKey( null );
        runFunction( "/test/getCurrentSiteConfig-test.js", "noCurrentSite" );
    }

    @Test
    public void currentSiteByContentPath()
    {
        final Content content = TestDataFixtures.newContent();
        this.portalRequest.setContent( null );
        this.portalRequest.setContentPath( content.getPath() );
        this.portalRequest.setSite( null );
        runFunction( "/test/getCurrentSiteConfig-test.js", "noCurrentSite" );
    }

    @Test
    public void testExample()
    {
        final Site site = TestDataFixtures.newSite().build();
        this.portalRequest.setSite( site );

        runScript( "/lib/xp/examples/portal/getSiteConfig.js" );
    }

    @Test
    public void configFromProject()
    {
        final Project project = TestDataFixtures.newDefaultProject().build();
        Mockito.when( projectService.get( ProjectName.from( ContextAccessor.current().getRepositoryId() ) ) ).thenReturn( project );

        runFunction( "/test/getCurrentSiteConfig-test.js", "configFromProject" );
    }
}
