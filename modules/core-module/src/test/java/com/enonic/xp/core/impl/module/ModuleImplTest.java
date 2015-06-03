package com.enonic.xp.core.impl.module;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import com.google.common.collect.Lists;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.inputtype.InputTypes;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleVersion;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.isA;

public class ModuleImplTest
{
    private Bundle bundle;

    @Before
    public void setup()
        throws Exception
    {
        this.bundle = mockBundle( "site.xml", "cms/parts/mypart/part.xml", "cms/pages/mypage/page.xml" );
    }

    @Test
    public void testCreateModule()
    {
        final Form config = Form.newForm().
            addFormItem( Input.create().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        final ModuleImpl module = new ModuleImpl();
        module.moduleKey = ModuleKey.from( "mymodule" );
        module.moduleVersion = ModuleVersion.from( "1.0.0" );
        module.displayName = "module display name";
        module.url = "http://enonic.net";
        module.vendorName = "Enonic";
        module.vendorUrl = "https://www.enonic.com";
        module.config = config;

        assertEquals( "mymodule", module.getKey().toString() );
        assertEquals( "module display name", module.getDisplayName() );
        assertEquals( "http://enonic.net", module.getUrl() );
        assertEquals( "Enonic", module.getVendorName() );
        assertEquals( "https://www.enonic.com", module.getVendorUrl() );
        assertEquals( InputTypes.TEXT_LINE, module.getConfig().getInput( "some-name" ).getInputType() );
    }

    @Test
    public void testGetResource()
    {
        final ModuleImpl module = new ModuleImpl();
        module.moduleKey = ModuleKey.from( "mymodule" );
        module.moduleVersion = ModuleVersion.from( "1.0.0" );
        module.bundle = this.bundle;

        assertNotNull( module.getResource( "site.xml" ) );
        assertNotNull( module.getResource( "cms/parts/mypart/part.xml" ) );
        assertNull( module.getResource( "part" ) );
        assertNull( module.getResource( "not/found.txt" ) );
    }

    @Test
    public void testGetResourcePaths()
    {
        final ModuleImpl module = new ModuleImpl();
        module.moduleKey = ModuleKey.from( "mymodule" );
        module.moduleVersion = ModuleVersion.from( "1.0.0" );
        module.bundle = this.bundle;

        final Set<String> set = module.getResourcePaths();
        assertNotNull( set );
        assertEquals( 3, set.size() );
        assertTrue( set.contains( "cms/parts/mypart/part.xml" ) );
    }

    private Bundle mockBundle( final String... resourcePaths )
    {
        final Bundle bundle = Mockito.mock( Bundle.class );
        final List<URL> urlList = Lists.newArrayList();
        for ( String resourcePath : resourcePaths )
        {
            try
            {
                final URL url = new URL( "http://109.0:1/" + resourcePath );
                urlList.add( url );
                Mockito.when( bundle.getResource( resourcePath ) ).thenReturn( url );
            }
            catch ( MalformedURLException e )
            {
                throw new RuntimeException( e );
            }
        }
        final Enumeration<URL> bundleEntries = Collections.enumeration( urlList );
        Mockito.when( bundle.findEntries( isA( String.class ), isA( String.class ), isA( Boolean.class ) ) ).thenReturn( bundleEntries );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );
        return bundle;
    }
}
