package com.enonic.wem.api.content.rendering;


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reusable class for both Page and PageTemplate to be used for the setup of a Page.
 */
public class PageRegions
    implements Iterable<Region>
{
    private Map<String, Region> regionsByName = new LinkedHashMap<>();

    @Override
    public Iterator<Region> iterator()
    {
        return null;
    }
}
