package com.enonic.xp.schema.content;

import com.google.common.annotations.Beta;

@Beta
public class GetAllContentTypesParams
{
    private boolean inlineMixinsToFormItems = false;

    public boolean isInlineMixinsToFormItems()
    {
        return inlineMixinsToFormItems;
    }

    public GetAllContentTypesParams inlineMixinsToFormItems( final boolean value )
    {
        inlineMixinsToFormItems = value;
        return this;
    }

}
