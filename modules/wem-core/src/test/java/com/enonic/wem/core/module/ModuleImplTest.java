package com.enonic.wem.core.module;

import java.io.File;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Files;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class ModuleImplTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File moduleDir;

    @Before
    public void setup()
        throws Exception
    {
        this.moduleDir = this.folder.newFolder( "mymodule-1.0.0" );
        createFile( new File( this.moduleDir, "module.xml" ) );
        createFile( new File( this.moduleDir, "component/mypart/part.xml" ) );
        createFile( new File( this.moduleDir, "component/mypage/page.xml" ) );
    }

    private void createFile( final File file )
        throws Exception
    {
        file.getParentFile().mkdirs();
        Files.touch( file );
    }

    @Test
    public void testCreateModule()
    {
        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        final Module module = new ModuleBuilder().
            moduleKey( ModuleKey.from( "mymodule-1.0.0" ) ).
            displayName( "module display name" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            config( config ).
            build();

        assertEquals( "mymodule-1.0.0", module.getKey().toString() );
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
            moduleKey( ModuleKey.from( "mymodule-1.0.0" ) ).
            moduleDir( this.moduleDir ).
            build();

        assertNotNull( module.getResource( "module.xml" ) );
        assertNotNull( module.getResource( "component/mypart/part.xml" ) );
        assertNull( module.getResource( "component" ) );
        assertNull( module.getResource( "not/found.txt" ) );
    }

    @Test
    public void testGetResourcePaths()
    {
        final Module module = new ModuleBuilder().
            moduleKey( ModuleKey.from( "mymodule-1.0.0" ) ).
            moduleDir( this.moduleDir ).
            build();

        final Set<String> set = module.getResourcePaths();
        assertNotNull( set );
        assertEquals( 3, set.size() );
        assertTrue( set.contains( "component/mypart/part.xml" ) );
    }
}
