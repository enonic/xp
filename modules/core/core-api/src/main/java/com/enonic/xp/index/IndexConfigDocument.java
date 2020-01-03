package com.enonic.xp.index;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyPath;

@PublicApi
public interface IndexConfigDocument
{
    String getAnalyzer();

    IndexConfig getConfigForPath( final PropertyPath dataPath );

    IndexConfig getConfigForPath( final IndexPath indexPath );

    AllTextIndexConfig getAllTextConfig();
}
