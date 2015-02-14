package com.enonic.xp.core.content.page.region;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.substringAfterLast;
import static org.apache.commons.lang.StringUtils.substringBeforeLast;

public final class RegionPath
{
    private static final String DIVIDER = "/";

    private final ComponentPath parentComponentPath;

    private final String regionName;

    private final String refString;

    private RegionPath( final ComponentPath parentComponentPath, final String regionName )
    {
        checkNotNull( regionName, "regionName cannot be null" );
        this.parentComponentPath = parentComponentPath;
        this.regionName = regionName;
        if ( parentComponentPath != null )
        {
            this.refString = parentComponentPath + "/" + regionName;
        }
        else
        {
            this.refString = regionName;
        }
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
        return refString;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || !( o instanceof RegionPath ) )
        {
            return false;
        }
        final RegionPath that = (RegionPath) o;
        return refString.equals( that.refString );
    }

    @Override
    public int hashCode()
    {
        return refString.hashCode();
    }

    public static RegionPath from( final String regionPath )
    {
        if ( regionPath.contains( DIVIDER ) )
        {
            final String componentPath = substringBeforeLast( regionPath, DIVIDER );
            final String regionName = substringAfterLast( regionPath, DIVIDER );
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
