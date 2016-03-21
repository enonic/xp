package com.enonic.xp.region;


import java.util.Iterator;

import org.junit.Test;

import com.google.common.collect.UnmodifiableIterator;

import com.enonic.xp.page.PageRegions;

import static org.junit.Assert.*;

public class PageRegionsTest
{
    @Test
    public void iterator()
    {
        final PageRegions regions = PageRegions.create().
            add( Region.create().name( "a-region" ).build() ).
            add( Region.create().name( "b-region" ).build() ).
            add( Region.create().name( "c-region" ).build() ).
            build();

        Iterator<Region> iterator = regions.iterator();
        Region nextRegion = iterator.next();
        assertNotNull( nextRegion );
        assertEquals( "a-region", nextRegion.getName() );

        nextRegion = iterator.next();
        assertNotNull( nextRegion );
        assertEquals( "b-region", nextRegion.getName() );

        nextRegion = iterator.next();
        assertNotNull( nextRegion );
        assertEquals( "c-region", nextRegion.getName() );

        assertFalse( iterator.hasNext() );
    }

    @Test
    public void getRegion()
    {
        final PageRegions regions = PageRegions.create().
            add( Region.create().name( "a-region" ).build() ).
            add( Region.create().name( "b-region" ).build() ).
            add( Region.create().name( "c-region" ).build() ).
            build();

        assertEquals( "a-region", regions.getRegion( "a-region" ).getName() );
        assertEquals( "b-region", regions.getRegion( "b-region" ).getName() );
        assertEquals( "c-region", regions.getRegion( "c-region" ).getName() );
        assertNull( regions.getRegion( "no-region" ) );
    }

    @Test
    public void componentPaths_one_level()
    {
        final PageRegions regions = PageRegions.create().
            add( Region.create().name( "a-region" ).
                add( PartComponent.create().name( ComponentName.from( "part-a-in-a" ) ).build() ).
                build() ).
            add( Region.create().name( "b-region" ).
                add( PartComponent.create().name( ComponentName.from( "part-a-in-b" ) ).build() ).
                add( PartComponent.create().name( ComponentName.from( "part-b-in-b" ) ).build() ).
                build() ).
            build();

        final Iterator<Region> iterator = regions.iterator();

        // verify: components in a-region
        Region nextRegion = iterator.next();
        UnmodifiableIterator<Component> components = nextRegion.getComponents().iterator();
        assertEquals( "a-region/0", components.next().getPath().toString() );

        // verify: components in b-region
        nextRegion = iterator.next();
        components = nextRegion.getComponents().iterator();
        assertEquals( "b-region/0", components.next().getPath().toString() );
        assertEquals( "b-region/1", components.next().getPath().toString() );
    }

    @Test
    public void componentPaths_two_levels()
    {
        final PageRegions regions = PageRegions.create().
            add( Region.create().name( "region-level-1" ).
                add( LayoutComponent.create().name( ComponentName.from( "layout-level-1" ) ).
                    regions( LayoutRegions.create().
                        add( Region.create().name( "region-level-2" ).
                            add( PartComponent.create().name( ComponentName.from( "part-level-2" ) ).build() ).
                            build() ).
                        build() ).
                    build() ).
                build() ).
            build();

        // verify
        final Region regionLevel1 = regions.iterator().next();
        final UnmodifiableIterator<Component> componentsLevel1 = regionLevel1.getComponents().iterator();
        final LayoutComponent layoutLevel1 = (LayoutComponent) componentsLevel1.next();
        assertEquals( "region-level-1/0", layoutLevel1.getPath().toString() );

        final LayoutRegions layoutRegions = layoutLevel1.getRegions();
        final Region regionLevel2 = layoutRegions.iterator().next();
        final UnmodifiableIterator<Component> componentsLevel2 = regionLevel2.getComponents().iterator();
        assertEquals( "region-level-1/0/region-level-2/0", componentsLevel2.next().getPath().toString() );
    }

    @Test
    public void getComponent()
    {
        final PageRegions regions = PageRegions.create().
            add( Region.create().
                name( "a-region" ).
                add( PartComponent.create().name( "part-1-in-region-a" ).build() ).
                build() ).
            add( Region.create().
                name( "b-region" ).
                add( PartComponent.create().name( "part-1-in-region-b" ).build() ).
                build() ).
            build();

        assertEquals( "part-1-in-region-a", regions.getComponent( ComponentPath.from( "a-region/0" ) ).getName().toString() );
        assertEquals( "part-1-in-region-b", regions.getComponent( ComponentPath.from( "b-region/0" ) ).getName().toString() );
        assertNull( regions.getComponent( ComponentPath.from( "a-region/1" ) ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getComponent_throws_exception()
    {
        final PageRegions regions = PageRegions.create().
            add( Region.create().
                name( "a-region" ).
                add( PartComponent.create().name( "part-1-in-region-a" ).build() ).
                add( PartComponent.create().name( "part-1-in-region-b" ).build() ).
                build() ).
            build();

        regions.getComponent( ComponentPath.from( "a-region/1/2/3" ) );
    }
}
