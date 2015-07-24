package com.enonic.xp.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.Assert.*;

public class ResourcesTest
{
    private static final String RESOURCE_URI_1 = "/";

    private static final String RESOURCE_URI_2 = "/a/b.txt";

    private static final String RESOURCE_URI_3 = "/a/c.txt";

    private ArrayList<Resource> list;

    private Resource resource1;

    private Resource resource2;

    private Resource resource3;

    private ResourceService resourceService;

    private ApplicationKey applicationKey;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void initList()
        throws Exception
    {
        final File modulesDir = temporaryFolder.newFolder( "modules" );
        writeFile( modulesDir, "mymodule-1.0.0/a/b.txt", "a/b.txt" );
        writeFile( modulesDir, "mymodule-1.0.0/a/c.txt", "a/c.txt" );
        final ResourceUrlRegistry registry = ResourceUrlTestHelper.mockModuleScheme();
        registry.modulesDir( modulesDir );

        applicationKey = ApplicationKey.from( "mymodule-1.0.0" );
        this.resourceService = Mockito.mock( ResourceService.class );

        mockResource( "mymodule-1.0.0:/a/b.txt" );
        mockResource( "mymodule-1.0.0:/a/c.txt" );
        mockResource( "mymodule-1.0.0:/" );

        resource1 = this.resourceService.getResource( ResourceKey.from( applicationKey, RESOURCE_URI_1 ) );
        resource2 = this.resourceService.getResource( ResourceKey.from( applicationKey, RESOURCE_URI_2 ) );
        resource3 = this.resourceService.getResource( ResourceKey.from( applicationKey, RESOURCE_URI_3 ) );

        this.list = new ArrayList();
        this.list.add( resource1 );
        this.list.add( resource2 );
        this.list.add( resource3 );
    }

    private static void writeFile( final File dir, final String path, final String value )
        throws Exception
    {
        final File file = new File( dir, path );
        file.getParentFile().mkdirs();
        ByteSource.wrap( value.getBytes( Charsets.UTF_8 ) ).copyTo( new FileOutputStream( file ) );
    }

    @Test
    public void fromEmpty()
    {
        Resources resources = Resources.empty();
        assertEquals( 0, resources.getSize() );
    }

    @Test
    public void fromIterable()
    {
        final Resources resources = Resources.from( (Iterable<Resource>) this.list );

        assertEquals( 3, resources.getSize() );
        assertEquals( resource1, resources.first() );
        assertNotNull( resources.getResource( ResourceKey.from( applicationKey, RESOURCE_URI_1 ) ) );
        assertNotNull( resources.getResource( ResourceKey.from( applicationKey, RESOURCE_URI_2 ) ) );
        assertNotNull( resources.getResource( ResourceKey.from( applicationKey, RESOURCE_URI_3 ) ) );
    }

    @Test
    public void fromCollection()
    {
        final Resources resources = Resources.from( this.list );

        assertEquals( 3, resources.getSize() );
        assertEquals( resource1, resources.first() );
        assertNotNull( resources.getResource( ResourceKey.from( applicationKey, RESOURCE_URI_1 ) ) );
        assertNotNull( resources.getResource( ResourceKey.from( applicationKey, RESOURCE_URI_2 ) ) );
        assertNotNull( resources.getResource( ResourceKey.from( applicationKey, RESOURCE_URI_3 ) ) );
    }

    @Test
    public void fromArrayList()
    {
        Resources resources = Resources.from( this.list.get( 0 ), this.list.get( 1 ), this.list.get( 2 ) );

        assertEquals( 3, resources.getSize() );
        assertEquals( resource1, resources.first() );
        assertNotNull( resources.getResource( ResourceKey.from( applicationKey, RESOURCE_URI_1 ) ) );
        assertNotNull( resources.getResource( ResourceKey.from( applicationKey, RESOURCE_URI_2 ) ) );
        assertNotNull( resources.getResource( ResourceKey.from( applicationKey, RESOURCE_URI_3 ) ) );
    }

    @Test
    public void getResourceKeys()
    {
        final Resources resources = Resources.from( this.list );

        final ResourceKeys resourceKeys = ResourceKeys.from( applicationKey + ":" + RESOURCE_URI_1,
                                                             applicationKey + ":" + RESOURCE_URI_2,
                                                             applicationKey + ":" + RESOURCE_URI_3 );

        assertEquals( resourceKeys, resources.getResourceKeys() );
    }

    @Test
    public void filter()
    {
        final Resources resources = Resources.from( this.list );
        final Resources filteredResources = resources.filter( resource -> resource2.equals( resource ) );

        assertEquals( 1, filteredResources.getSize() );
        assertEquals( resource2, filteredResources.first() );
    }

    protected void mockResource( String uri ) throws Exception {
        ResourceKey key = ResourceKey.from( uri );
        Resource res = new Resource( ResourceKey.from( uri ), new URL( "module:" + uri ) );
        Mockito.when( this.resourceService.getResource( key ) ).thenReturn( res );
    }

}