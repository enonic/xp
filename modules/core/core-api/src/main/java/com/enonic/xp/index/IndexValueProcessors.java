package com.enonic.xp.index;

import java.util.HashMap;
import java.util.Map;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class IndexValueProcessors
{
    public final static IndexValueProcessor HTML_STRIPPER = new HtmlStripper();

    private static final Map<String, IndexValueProcessor> PROCESSORS = new HashMap<>();

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
