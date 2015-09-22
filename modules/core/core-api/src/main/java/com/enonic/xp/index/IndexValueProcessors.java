package com.enonic.xp.index;

import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.collect.Maps;

@Beta
public final class IndexValueProcessors
{
    public final static IndexValueProcessor HTML_STRIPPER = new HtmlStripper();

    private static final Map<String, IndexValueProcessor> PROCESSORS = Maps.newHashMap();

    static
    {
        register( HTML_STRIPPER );
    }

    private static void register( final IndexValueProcessor processor )
    {
        PROCESSORS.put( processor.getName(), processor );
    }

    public static IndexValueProcessor get( final String name )
    {
        return PROCESSORS.get( name );
    }
}
