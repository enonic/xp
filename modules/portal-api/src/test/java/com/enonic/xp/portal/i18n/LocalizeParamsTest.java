package com.enonic.xp.portal.i18n;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.site.Site;

public class LocalizeParamsTest
    extends Assert
{
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private PortalRequest request;

    @Before
    public void setUp()
        throws Exception
    {
        request = new PortalRequest();
        request.setSite( Site.create().
            name( ContentName.from( "test" ) ).
            parentPath( ContentPath.ROOT ).
            language( DEFAULT_LOCALE ).
            build() );

        PortalRequestAccessor.set( request );
    }

    @Test
    public void testName()
        throws Exception
    {
        final ViewFunctionParams viewParams = new ViewFunctionParams().
            name( "test" ).
            args( Lists.newArrayList( "_key=fisk", "_values={'a',2,'b'}" ) ).
            portalRequest( this.request );

        LocalizeParams params = new LocalizeParams( this.request ).setAsMap( viewParams.getArgs() );

        params.getApplicationKey();

    }
}