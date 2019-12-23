package com.enonic.xp.portal.impl.rendering;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutRegions;
import com.enonic.xp.region.Region;

public final class FragmentPageResolver
{

    public Page inlineFragmentInPage( final Page page, final Component fragmentComponent, final ComponentPath path )
    {
        ImmutableList<ComponentPath.RegionAndComponent> pathItems = ImmutableList.copyOf( path );
        if ( page.getRegions() != null )
        {
            final PageRegions regions = this.replaceComponentInPage( page.getRegions(), pathItems, fragmentComponent );
            return Page.create( page ).regions( regions ).build();
        }
        else if ( page.getFragment() != null && page.getFragment() instanceof LayoutComponent )
        {
            final LayoutComponent layoutComponent = (LayoutComponent) page.getFragment();
            final LayoutRegions layoutRegions = this.replaceComponentInLayout( layoutComponent.getRegions(), pathItems, fragmentComponent );
            final LayoutComponent updatedLayout = LayoutComponent.create( layoutComponent ).regions( layoutRegions ).build();
            return Page.create( page ).fragment( updatedLayout ).build();
        }
        return page;
    }

    private PageRegions replaceComponentInPage( final PageRegions pageRegions,
                                                final ImmutableList<ComponentPath.RegionAndComponent> pathItems, final Component component )
    {
        final ComponentPath.RegionAndComponent regionCmp = pathItems.isEmpty() ? null : pathItems.get( 0 );
        final Region region = regionCmp == null ? null : pageRegions.getRegion( regionCmp.getRegionName() );
        if ( region == null )
        {
            return pageRegions;
        }

        final int componentIndex = regionCmp.getComponentIndex();
        final Component existingCmp = region.getComponent( componentIndex );
        if ( existingCmp == null )
        {
            return pageRegions;
        }

        final int numberOfLevels = pathItems.size();
        if ( numberOfLevels == 1 )
        {
            final Region updatedRegion = Region.create( region ).set( componentIndex, component ).build();
            return replaceRegionInPage( pageRegions, region, updatedRegion );
        }
        else
        {
            if ( !( existingCmp instanceof LayoutComponent ) )
            {
                return pageRegions;
            }
            final LayoutComponent layoutComponent = (LayoutComponent) existingCmp;
            final LayoutRegions layoutRegions =
                replaceComponentInLayout( layoutComponent.getRegions(), removeFirstLevel( pathItems ), component );
            final LayoutComponent updatedLayout = LayoutComponent.create( layoutComponent ).regions( layoutRegions ).build();

            final Region updatedRegion = Region.create( region ).set( componentIndex, updatedLayout ).build();
            return replaceRegionInPage( pageRegions, region, updatedRegion );
        }
    }

    private LayoutRegions replaceComponentInLayout( final LayoutRegions layoutRegions,
                                                    final ImmutableList<ComponentPath.RegionAndComponent> pathItems,
                                                    final Component component )
    {
        final ComponentPath.RegionAndComponent regionCmp = pathItems.isEmpty() ? null : pathItems.get( 0 );
        final Region region = regionCmp == null ? null : layoutRegions.getRegion( regionCmp.getRegionName() );
        if ( region == null )
        {
            return layoutRegions.copy();
        }

        final int componentIndex = regionCmp.getComponentIndex();
        final Component existingCmp = region.getComponent( componentIndex );
        if ( existingCmp != null )
        {
            final Region updatedRegion = Region.create( region ).set( componentIndex, component ).build();
            return replaceRegionInLayout( layoutRegions, region, updatedRegion );
        }
        return layoutRegions.copy();
    }

    private LayoutRegions replaceRegionInLayout( final LayoutRegions regions, final Region sourceRegion, final Region newRegion )
    {
        final LayoutRegions.Builder result = LayoutRegions.create();
        for ( Region region : regions )
        {
            if ( region != sourceRegion )
            {
                result.add( region );
            }
            else
            {
                result.add( newRegion );
            }
        }
        return result.build();
    }

    private PageRegions replaceRegionInPage( final PageRegions regions, final Region sourceRegion, final Region newRegion )
    {
        final PageRegions.Builder result = PageRegions.create();
        for ( Region region : regions )
        {
            if ( region != sourceRegion )
            {
                result.add( region );
            }
            else
            {
                result.add( newRegion );
            }
        }
        return result.build();
    }

    private ImmutableList<ComponentPath.RegionAndComponent> removeFirstLevel(
        final ImmutableList<ComponentPath.RegionAndComponent> pathElements )
    {
        if ( pathElements.size() <= 1 )
        {
            return ImmutableList.of();
        }
        else
        {
            return pathElements.subList( 1, pathElements.size() );
        }
    }
}
