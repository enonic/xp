package com.enonic.wem.web.rest.account;

import com.enonic.wem.web.rest.common.RestResponse;

public class IndexRestResponse extends RestResponse
{
    private boolean indexing;

    private int progress;

    public boolean isIndexing()
    {
        return indexing;
    }

    public void setIndexing( boolean indexing )
    {
        this.indexing = indexing;
    }

    public int getProgress()
    {
        return progress;
    }

    public void setProgress( int progress )
    {
        this.progress = progress;
    }
}
