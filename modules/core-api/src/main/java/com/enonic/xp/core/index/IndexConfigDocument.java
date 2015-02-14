package com.enonic.xp.core.index;

import com.enonic.xp.core.data.PropertyPath;

public interface IndexConfigDocument
{
    public String getAnalyzer();

    public IndexConfig getConfigForPath( final PropertyPath dataPath );
}
