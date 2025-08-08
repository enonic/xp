package com.enonic.xp.index;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface IndexConfigDocument
{
    String getAnalyzer();

    IndexConfig getConfigForPath( IndexPath indexPath );

    AllTextIndexConfig getAllTextConfig();
}
