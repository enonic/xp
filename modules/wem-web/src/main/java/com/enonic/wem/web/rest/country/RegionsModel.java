package com.enonic.wem.web.rest.country;

import java.util.ArrayList;
import java.util.List;

public class RegionsModel
{
    private int total;
    private List<RegionModel> regions;

    public RegionsModel()
    {
        this.regions = new ArrayList<RegionModel>();
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<RegionModel> getRegions()
    {
        return regions;
    }

    public void setRegions( List<RegionModel> regions )
    {
        this.regions = regions;
    }

    public void addRegion(RegionModel region)
    {
        this.regions.add(region);
    }
}
