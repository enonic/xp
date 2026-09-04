package com.enonic.xp.core.impl.app;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationHelperTest
    extends BundleBasedTest
{
    @Test
    void getApplicationType_static()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", newBundle( "app1", true ).addResource( "enonic.yaml", yaml( "type: \"Static\"" ) ) );

        assertEquals( ApplicationType.STATIC, ApplicationHelper.getApplicationType( bundle ) );
    }

    @Test
    void getApplicationType_bundle()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", newBundle( "app1", true ).addResource( "enonic.yaml", yaml( "type: \"Bundle\"" ) ) );

        assertEquals( ApplicationType.BUNDLE, ApplicationHelper.getApplicationType( bundle ) );
    }

    @Test
    void getApplicationType_default()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", newBundle( "app1", true ).addResource( "application.yaml", yaml( "title: \"App\"" ) ) );

        assertEquals( ApplicationType.BUNDLE, ApplicationHelper.getApplicationType( bundle ) );
    }

    @Test
    void getApplicationType_no_descriptor()
    {
        final Bundle bundle = deploy( "app1", newBundle( "app1", true ) );

        assertEquals( ApplicationType.BUNDLE, ApplicationHelper.getApplicationType( bundle ) );
    }

    @Test
    void getApplicationType_invalid_descriptor()
        throws Exception
    {
        final Bundle bundle = deploy( "app1", newBundle( "app1", true ).addResource( "enonic.yaml", yaml( "type: \"Virtual\"" ) ) );

        assertEquals( ApplicationType.BUNDLE, ApplicationHelper.getApplicationType( bundle ) );
    }

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

    private static InputStream yaml( final String line )
        throws IOException
    {
        return ByteSource.wrap( ( "kind: \"Application\"\n" + line + "\n" ).getBytes( StandardCharsets.UTF_8 ) ).openStream();
    }
}
