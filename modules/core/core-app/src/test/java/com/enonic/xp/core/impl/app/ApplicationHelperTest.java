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

    @Test
    void isSchemaApplication_global_cms_without_bnd_headers()
    {
        final Bundle bundle =
            deploy( "app1", buildWithoutBnd( newBundle( "app1", true ).addResource( "cms/cms.yaml", stream( "kind: \"CMS\"" ) ) ) );

        assertTrue( ApplicationHelper.isSchemaApplication( bundle ) );
    }

    @Test
    void isSchemaApplication_built_with_bnd_last_modified()
    {
        final Bundle bundle = deploy( "app1", buildWithoutBnd( newBundle( "app1", true ).setHeader( "Bnd-LastModified", "1783360499694" )
                                                                  .addResource( "cms/cms.yaml", stream( "kind: \"CMS\"" ) ) ) );

        assertFalse( ApplicationHelper.isSchemaApplication( bundle ) );
    }

    @Test
    void isSchemaApplication_built_with_bnd_tool()
    {
        final Bundle bundle = deploy( "app1", buildWithoutBnd( newBundle( "app1", true ).setHeader( "Tool", "Bnd-7.3.0.202606021345" )
                                                                  .addResource( "cms/cms.yaml", stream( "kind: \"CMS\"" ) ) ) );

        assertFalse( ApplicationHelper.isSchemaApplication( bundle ) );
    }

    @Test
    void isSchemaApplication_other_tool()
    {
        final Bundle bundle = deploy( "app1", buildWithoutBnd( newBundle( "app1", true ).setHeader( "Tool", "Enonic CLI" )
                                                                  .addResource( "cms/cms.yaml", stream( "kind: \"CMS\"" ) ) ) );

        assertTrue( ApplicationHelper.isSchemaApplication( bundle ) );
    }

    @Test
    void isSchemaApplication_without_cms_descriptor()
    {
        final Bundle bundle = deploy( "app1", buildWithoutBnd( newBundle( "app1", true ) ) );

        assertFalse( ApplicationHelper.isSchemaApplication( bundle ) );
    }

    @Test
    void isSchemaApplication_local()
    {
        final Bundle bundle = deploy( ApplicationHelper.toBundleLocation( ApplicationKey.from( "app1" ), true ),
                                      buildWithoutBnd( newBundle( "app1", true ).addResource( "cms/cms.yaml", stream( "kind: \"CMS\"" ) ) ) );

        assertFalse( ApplicationHelper.isSchemaApplication( bundle ) );
    }

    private static InputStream stream( final String content )
    {
        return new ByteArrayInputStream( content.getBytes( StandardCharsets.UTF_8 ) );
    }
}