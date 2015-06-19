package com.enonic.xp.lib.portal.current;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.site.Site;
import com.enonic.xp.testing.script.ScriptTestSupport;

@Ignore
public class PortalServiceScriptTest
    extends ScriptTestSupport
{
    @Before
    public void setUp()
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMode( RenderMode.LIVE );
        portalRequest.setBranch( Branch.from( "draft" ) );
        portalRequest.setModule( ModuleKey.from( "mymodule" ) );
        portalRequest.setBaseUri( "/portal" );

        final Content content = Content.newContent().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        portalRequest.setContent( content );

        final PartComponent component = PartComponent.
            newPartComponent().
            name( "mycomp" ).
            build();
        final Region region = Region.
            newRegion().
            name( "main" ).
            add( component ).
            build();
        component.setRegion( region );
        portalRequest.setComponent( component );

        portalRequest.setSite( Site.newSite().
            name( ContentName.from( "test" ) ).
            parentPath( ContentPath.ROOT ).
            language( Locale.ENGLISH ).
            build() );

        PortalRequestAccessor.set( portalRequest );

        final PortalServiceWrapper service = new PortalServiceWrapper();

        addBean( "com.enonic.xp.lib.portal.url.UrlServiceWrapper", new Object() );
        addBean( "com.enonic.xp.lib.portal.current.PortalServiceWrapper", service );
    }

    private boolean execute( final String method )
        throws Exception
    {
        final ScriptExports exports = runTestScript( "test/current-test.js" );
        final ScriptValue value = exports.executeMethod( method );
        return value != null ? value.getValue( Boolean.class ) : false;
    }

    @Test
    public void getContentTest()
        throws Exception
    {
        Assert.assertTrue( execute( "getContentTest" ) );
    }

    @Test
    public void getComponentTest()
        throws Exception
    {
        Assert.assertTrue( execute( "getComponentTest" ) );
    }

    @Test
    public void getSiteTest()
        throws Exception
    {
        Assert.assertTrue( execute( "getSiteTest" ) );
    }
}
