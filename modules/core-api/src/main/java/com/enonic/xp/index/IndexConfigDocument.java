package com.enonic.xp.index;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.PropertyPath;

@Beta
public interface IndexConfigDocument
{
    String getAnalyzer();

    IndexConfig getConfigForPath( final PropertyPath dataPath );
}
