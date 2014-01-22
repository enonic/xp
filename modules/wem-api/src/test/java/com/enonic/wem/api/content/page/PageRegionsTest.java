package com.enonic.wem.api.content.page;


import java.util.Iterator;

import org.junit.Test;

import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.region.Region;

import static com.enonic.wem.api.content.page.PageRegions.newPageRegions;
import static com.enonic.wem.api.content.page.region.Region.newRegion;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class PageRegionsTest
{
    @Test
    public void iterator()
    {
        PageRegions regions = newPageRegions().
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
        PageRegions regions = newPageRegions().
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
    public void getComponent()
    {
        PageRegions regions = newPageRegions().
            add( newRegion().
                name( "a-region" ).
                add( PartComponent.newPartComponent().name( "part-1-in-region-a" ).build() ).
                build() ).
            add( newRegion().
                name( "b-region" ).
                add( PartComponent.newPartComponent().name( "part-1-in-region-b" ).build() ).
                build() ).
            build();

        assertEquals( "part-1-in-region-a", regions.getComponent( ComponentName.from( "part-1-in-region-a" ) ).getName().toString() );
        assertEquals( "part-1-in-region-b", regions.getComponent( ComponentName.from( "part-1-in-region-b" ) ).getName().toString() );
        assertNull( regions.getComponent( ComponentName.from( "no-part" ) ) );
    }
}
