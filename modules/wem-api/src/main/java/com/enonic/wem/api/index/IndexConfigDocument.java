package com.enonic.wem.api.index;

import com.enonic.wem.api.data.PropertyPath;

public interface IndexConfigDocument
{
    public String getAnalyzer();

    public IndexConfig getConfigForPath( final PropertyPath dataPath );
}
