package com.enonic.xp.index;

import java.util.HashMap;
import java.util.Map;

public class IndexValueProcessorRegistry
{
    private static final Map<String, IndexValueProcessor> indexValueProcessorMap = new HashMap<String, IndexValueProcessor>();

    public static synchronized void register( IndexValueProcessor indexValueProcessor )
    {
        indexValueProcessorMap.put( indexValueProcessor.getName(), indexValueProcessor );
    }

    public static synchronized IndexValueProcessor unregister( IndexValueProcessor indexValueProcessor )
    {
        return indexValueProcessorMap.remove( indexValueProcessor.getName() );
    }

    public static synchronized IndexValueProcessor getIndexValueProcessor( String indexValueProcessorName )
    {
        return indexValueProcessorMap.get( indexValueProcessorName );
    }
}