package com.enonic.wem.api.index;

import com.enonic.wem.api.data.DataPath;

public interface IndexConfigDocumentNew
{
    public String getAnalyzer();

    public IndexConfig getConfigForData( final DataPath dataPath );
}
