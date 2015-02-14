package com.enonic.xp.module.impl;

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

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.xp.module.impl.ModuleBuilder;

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
        this.bundle = mockBundle( "module.xml", "cms/parts/mypart/part.xml", "cms/pages/mypage/page.xml" );
    }

    @Test
    public void testCreateModule()
    {
        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        final Module module = new ModuleBuilder().
            moduleKey( ModuleKey.from( "mymodule" ) ).
            moduleVersion( ModuleVersion.from( "1.0.0" ) ).
            displayName( "module display name" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            config( config ).
            build();

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
        final Module module = new ModuleBuilder().
            moduleKey( ModuleKey.from( "mymodule" ) ).
            moduleVersion( ModuleVersion.from( "1.0.0" ) ).
            bundle( bundle ).
            build();

        assertNotNull( module.getResource( "module.xml" ) );
        assertNotNull( module.getResource( "cms/parts/mypart/part.xml" ) );
        assertNull( module.getResource( "part" ) );
        assertNull( module.getResource( "not/found.txt" ) );
    }

    @Test
    public void testGetResourcePaths()
    {
        final Module module = new ModuleBuilder().
            moduleKey( ModuleKey.from( "mymodule" ) ).
            moduleVersion( ModuleVersion.from( "1.0.0" ) ).
            bundle( bundle ).
            build();

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
