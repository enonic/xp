package com.enonic.xp.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

import static org.junit.Assert.*;

public class ResourcesTest
{
    private static final String RESOURCE_URI_1 = "myapplication-1.0.0:";

    private static final String RESOURCE_URI_2 = "myapplication-1.0.0:/a/b.txt";

    private static final String RESOURCE_URI_3 = "myapplication-1.0.0:/a/c.txt";

    private ArrayList<Resource> list;

    private Resource resource1;

    private Resource resource2;

    private Resource resource3;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void initList()
        throws Exception
    {
        final File applicationsDir = temporaryFolder.newFolder( "applications" );
        writeFile( applicationsDir, "myapplication-1.0.0/a/b.txt", "a/b.txt" );
        writeFile( applicationsDir, "myapplication-1.0.0/a/c.txt", "a/c.txt" );

        final ResourceKey resourceKey1 = ResourceKey.from( RESOURCE_URI_1 );
        final ResourceKey resourceKey2 = ResourceKey.from( RESOURCE_URI_2 );
        final ResourceKey resourceKey3 = ResourceKey.from( RESOURCE_URI_3 );
        resource1 = new Resource( resourceKey1, new File( applicationsDir, "myapplication-1.0.0" ).toURI().toURL() );
        resource2 = new Resource( resourceKey2, new File( applicationsDir, "myapplication-1.0.0/a/b.txt" ).toURI().toURL() );
        resource3 = new Resource( resourceKey3, new File( applicationsDir, "myapplication-1.0.0/a/c.txt" ).toURI().toURL() );

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
        assertNotNull( resources.getResource( ResourceKey.from( RESOURCE_URI_1 ) ) );
        assertNotNull( resources.getResource( ResourceKey.from( RESOURCE_URI_2 ) ) );
        assertNotNull( resources.getResource( ResourceKey.from( RESOURCE_URI_3 ) ) );
    }

    @Test
    public void fromCollection()
    {
        final Resources resources = Resources.from( this.list );

        assertEquals( 3, resources.getSize() );
        assertEquals( resource1, resources.first() );
        assertNotNull( resources.getResource( ResourceKey.from( RESOURCE_URI_1 ) ) );
        assertNotNull( resources.getResource( ResourceKey.from( RESOURCE_URI_2 ) ) );
        assertNotNull( resources.getResource( ResourceKey.from( RESOURCE_URI_3 ) ) );
    }

    @Test
    public void fromArrayList()
    {
        Resources resources = Resources.from( this.list.get( 0 ), this.list.get( 1 ), this.list.get( 2 ) );

        assertEquals( 3, resources.getSize() );
        assertEquals( resource1, resources.first() );
        assertNotNull( resources.getResource( ResourceKey.from( RESOURCE_URI_1 ) ) );
        assertNotNull( resources.getResource( ResourceKey.from( RESOURCE_URI_2 ) ) );
        assertNotNull( resources.getResource( ResourceKey.from( RESOURCE_URI_3 ) ) );
    }

    @Test
    public void getResourceKeys()
    {
        final Resources resources = Resources.from( this.list );

        final ResourceKeys resourceKeys = ResourceKeys.from( RESOURCE_URI_1, RESOURCE_URI_2, RESOURCE_URI_3 );

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

}
