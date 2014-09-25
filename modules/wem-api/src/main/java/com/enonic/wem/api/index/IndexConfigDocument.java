package com.enonic.wem.api.index;

import com.enonic.wem.api.data.DataPath;

public interface IndexConfigDocument
{
    public String getAnalyzer();

    public IndexConfig getConfigForPath( final DataPath dataPath );
}
