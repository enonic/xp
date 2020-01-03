package com.enonic.xp.portal.impl.view;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.site.Site;

public class LocalizeParamsTest
{
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private PortalRequest request;

    @BeforeEach
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
            args( List.of( "_key=fisk", "_values={'a',2,'b'}" ) ).
            portalRequest( this.request );

        LocalizeParams params = new LocalizeParams( this.request ).setAsMap( viewParams.getArgs() );

        params.getApplicationKey();

    }
}
