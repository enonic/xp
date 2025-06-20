package com.enonic.xp.lib.portal.current;

import java.time.Instant;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.security.PrincipalKey;
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
        final Content project = Content.create()
            .id( ContentId.from( "0123456789" ) )
            .name( ContentName.from( "content" ) )
            .parentPath( ContentPath.ROOT )
            .modifier( PrincipalKey.from( "user:system:admin" ) )
            .modifiedTime( Instant.ofEpochSecond( 0 ) )
            .creator( PrincipalKey.from( "user:system:admin" ) )
            .createdTime( Instant.ofEpochSecond( 0 ) )
            .language( Locale.ENGLISH )
            .data( createProjectData() )
            .build();

        this.portalRequest.setSite( project );

        runFunction( "/test/getCurrentSiteConfig-test.js", "configFromProject" );
    }

    private PropertyTree createProjectData()
    {
        final PropertyTree data = new PropertyTree();

        final PropertyTree config = new PropertyTree();
        config.setLong( "long", 42L );
        config.addBoolean( "boolean", true );
        config.setString( "string", "my-string" );

        final PropertySet set = data.newSet();
        set.addString( "applicationKey", "myapplication" );
        set.addSet( "config", config.getRoot().copy( data ) );

        data.setSet( "siteConfig", set );

        return data;
    }
}
