package com.enonic.xp.region;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Preconditions.checkNotNull;

@PublicApi
public final class RegionPath
{
    private static final String DIVIDER = "/";

    private final ComponentPath parentComponentPath;

    private final String regionName;

    private RegionPath( final ComponentPath parentComponentPath, final String regionName )
    {
        checkNotNull( regionName, "regionName cannot be null" );
        this.parentComponentPath = parentComponentPath;
        this.regionName = regionName;

    }

    public ComponentPath getParentComponentPath()
    {
        return parentComponentPath;
    }

    public String getRegionName()
    {
        return regionName;
    }

    @Override
    public String toString()
    {
        return parentComponentPath == null ? regionName : parentComponentPath + "/" + regionName;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof RegionPath ) )
        {
            return false;
        }
        final RegionPath that = (RegionPath) o;
        return Objects.equals( parentComponentPath, that.parentComponentPath ) && Objects.equals( regionName, that.regionName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( parentComponentPath, regionName );
    }

    public static RegionPath from( final String regionPath )
    {
        final int dividerIndex = regionPath.lastIndexOf( DIVIDER );
        if ( dividerIndex > -1 )
        {
            final String componentPath = regionPath.substring( 0, dividerIndex );
            final String regionName = regionPath.substring( dividerIndex + 1 );
            return new RegionPath( ComponentPath.from( componentPath ), regionName );
        }
        else
        {
            return new RegionPath( null, regionPath );
        }
    }

    public static RegionPath from( final ComponentPath parentComponentPath, final String regionName )
    {
        return new RegionPath( parentComponentPath, regionName );
    }
}
