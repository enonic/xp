package com.enonic.xp.page;


import java.util.Iterator;

import org.junit.Test;

import com.google.common.collect.UnmodifiableIterator;

import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentName;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.Region;

import static com.enonic.xp.page.PageRegions.newPageRegions;
import static com.enonic.xp.region.LayoutComponent.newLayoutComponent;
import static com.enonic.xp.region.LayoutRegions.newLayoutRegions;
import static com.enonic.xp.region.PartComponent.newPartComponent;
import static com.enonic.xp.region.Region.newRegion;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class PageRegionsTest
{
    @Test
    public void iterator()
    {
        final PageRegions regions = newPageRegions().
            add( newRegion().name( "a-region" ).build() ).
            add( newRegion().name( "b-region" ).build() ).
            add( newRegion().name( "c-region" ).build() ).
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
        final PageRegions regions = newPageRegions().
            add( newRegion().name( "a-region" ).build() ).
            add( newRegion().name( "b-region" ).build() ).
            add( newRegion().name( "c-region" ).build() ).
            build();

        assertEquals( "a-region", regions.getRegion( "a-region" ).getName() );
        assertEquals( "b-region", regions.getRegion( "b-region" ).getName() );
        assertEquals( "c-region", regions.getRegion( "c-region" ).getName() );
        assertNull( regions.getRegion( "no-region" ) );
    }

    @Test
    public void componentPaths_one_level()
    {
        final PageRegions regions = newPageRegions().
            add( newRegion().name( "a-region" ).
                add( newPartComponent().name( ComponentName.from( "part-a-in-a" ) ).build() ).
                build() ).
            add( newRegion().name( "b-region" ).
                add( newPartComponent().name( ComponentName.from( "part-a-in-b" ) ).build() ).
                add( newPartComponent().name( ComponentName.from( "part-b-in-b" ) ).build() ).
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
        final PageRegions pageRegions = newPageRegions().
            add( newRegion().name( "region-level-1" ).
                add( newLayoutComponent().name( ComponentName.from( "layout-level-1" ) ).
                    regions( newLayoutRegions().
                        add( newRegion().name( "region-level-2" ).
                            add( newPartComponent().name( ComponentName.from( "part-level-2" ) ).build() ).
                            build() ).
                        build() ).
                    build() ).
                build() ).
            build();

        // verify
        final Region regionLevel1 = pageRegions.iterator().next();
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
        final PageRegions regions = newPageRegions().
            add( newRegion().
                name( "a-region" ).
                add( newPartComponent().name( "part-1-in-region-a" ).build() ).
                build() ).
            add( newRegion().
                name( "b-region" ).
                add( newPartComponent().name( "part-1-in-region-b" ).build() ).
                build() ).
            build();

        assertEquals( "part-1-in-region-a", regions.getComponent( ComponentPath.from( "a-region/0" ) ).getName().toString() );
        assertEquals( "part-1-in-region-b", regions.getComponent( ComponentPath.from( "b-region/0" ) ).getName().toString() );
        assertNull( regions.getComponent( ComponentPath.from( "a-region/1" ) ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getComponent_throws_exception()
    {
        final PageRegions regions = newPageRegions().
            add( newRegion().
                name( "a-region" ).
                add( newPartComponent().name( "part-1-in-region-a" ).build() ).
                add( newPartComponent().name( "part-1-in-region-b" ).build() ).
                build() ).
            build();

        regions.getComponent( ComponentPath.from( "a-region/1/2/3" ) );
    }
}
