package com.enonic.xp.portal.impl.view;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.site.Site;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalizeParamsTest
{
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private PortalRequest request;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        request = new PortalRequest();
        request.setSite(
            Site.create().name( ContentName.from( "test" ) ).parentPath( ContentPath.ROOT ).language( DEFAULT_LOCALE ).build() );
    }

    @Test
    public void testArgs()
        throws Exception
    {
        request.setApplicationKey( ApplicationKey.from( "foo" ) );
        final ViewFunctionParams viewParams = new ViewFunctionParams()
            .args( List.of( "_key=fisk", "_values={a,2,b}" ) );

        LocalizeParams params = new LocalizeParams( this.request ).setAsMap( viewParams.getArgs() );

        assertEquals( ApplicationKey.from( "foo" ), params.getApplicationKey() );
        assertEquals( "fisk", params.getKey() );
        assertThat( params.getParams() ).containsExactly( "a", "2", "b" );
    }
}
