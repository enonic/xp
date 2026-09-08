package com.enonic.xp.core.impl.app;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationHelperTest
    extends BundleBasedTest
{
    @Test
    void hasCmsDescriptor_yaml()
    {
        final Bundle bundle = deploy( "app1", newBundle( "app1", true ).addResource( "cms/cms.yaml", stream( "kind: \"CMS\"" ) ) );

        assertTrue( ApplicationHelper.hasCmsDescriptor( bundle ) );
    }

    @Test
    void hasCmsDescriptor_yml()
    {
        final Bundle bundle = deploy( "app1", newBundle( "app1", true ).addResource( "cms/cms.yml", stream( "kind: \"CMS\"" ) ) );

        assertTrue( ApplicationHelper.hasCmsDescriptor( bundle ) );
    }

    @Test
    void hasCmsDescriptor_missing()
    {
        final Bundle bundle = deploy( "app1", newBundle( "app1", true ).addResource( "cms.yaml", stream( "kind: \"CMS\"" ) )
            .addResource( "cms/content-types/mytype/mytype.yaml", stream( "kind: \"ContentType\"" ) ) );

        assertFalse( ApplicationHelper.hasCmsDescriptor( bundle ) );
    }

    @Test
    void isLocalApplication_by_bundle_location()
    {
        final ApplicationKey appKey = ApplicationKey.from( "app1" );

        assertEquals( "app1", ApplicationHelper.toBundleLocation( appKey, false ) );
        assertEquals( "local:app1", ApplicationHelper.toBundleLocation( appKey, true ) );

        final Bundle global = deploy( ApplicationHelper.toBundleLocation( appKey, false ), newBundle( "app1", true ) );
        assertFalse( ApplicationHelper.isLocalApplication( global ) );
        assertEquals( appKey, ApplicationHelper.getApplicationKey( global ) );

        final Bundle local = deploy( ApplicationHelper.toBundleLocation( appKey, true ), newBundle( "app1", true, "1.0.1" ) );
        assertTrue( ApplicationHelper.isLocalApplication( local ) );
        assertEquals( appKey, ApplicationHelper.getApplicationKey( local ) );
    }

    private static InputStream stream( final String content )
    {
        return new ByteArrayInputStream( content.getBytes( StandardCharsets.UTF_8 ) );
    }
}