package com.enonic.wem.api.content.rendering;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Region
    implements Iterable<Component>
{
    private List<Component> components = new ArrayList<>();

    @Override
    public Iterator<Component> iterator()
    {
        return null;
    }
}
