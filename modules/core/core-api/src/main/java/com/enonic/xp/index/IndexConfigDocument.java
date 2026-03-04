package com.enonic.xp.index;

public interface IndexConfigDocument
{
    String getAnalyzer();

    IndexConfig getConfigForPath( IndexPath indexPath );

    AllTextIndexConfig getAllTextConfig();
}
