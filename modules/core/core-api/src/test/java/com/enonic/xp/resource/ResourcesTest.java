package com.enonic.xp.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class ResourcesTest
{
    private Resource resource1;

    private Resource resource2;

    private Resource resource3;

    @Before
    public void initList()
    {
        this.resource1 = mockResource( "myapp:/a.txt" );
        this.resource2 = mockResource( "myapp:/a/b.txt" );
        this.resource3 = mockResource( "myapp:/a/c.txt" );
    }

    private Resource mockResource( final String name )
    {
        final Resource resource = Mockito.mock( Resource.class );
        Mockito.when( resource.getKey() ).thenReturn( ResourceKey.from( name ) );
        return resource;
    }

    @Test
    public void fromEmpty()
    {
        Resources resources = Resources.empty();
        assertEquals( 0, resources.getSize() );
    }

    @Test
    public void testFrom()
    {
        Resources resources = Resources.from( this.resource1, this.resource2, this.resource3 );
        assertEquals( 3, resources.getSize() );
        assertEquals( resource1, resources.first() );

        resources = Resources.from( Lists.newArrayList( this.resource1, this.resource2, this.resource3 ) );
        assertEquals( 3, resources.getSize() );
        assertEquals( resource1, resources.first() );
    }

    @Test
    public void getResourceKeys()
    {
        final Resources resources = Resources.from( this.resource1, this.resource2, this.resource3 );
        final ResourceKeys resourceKeys = ResourceKeys.from( "myapp:/a.txt", "myapp:/a/b.txt", "myapp:/a/c.txt" );

        assertEquals( resourceKeys, resources.getResourceKeys() );
    }

    @Test
    public void filter()
    {
        final Resources resources = Resources.from( this.resource1, this.resource2, this.resource3 );
        final Resources filteredResources = resources.filter( resource -> this.resource2.equals( resource ) );

        assertEquals( 1, filteredResources.getSize() );
        assertEquals( this.resource2, filteredResources.first() );
    }

}
