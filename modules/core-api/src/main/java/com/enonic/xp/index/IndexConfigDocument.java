package com.enonic.xp.index;

import com.enonic.xp.data.PropertyPath;

public interface IndexConfigDocument
{
    public String getAnalyzer();

    public IndexConfig getConfigForPath( final PropertyPath dataPath );
}
