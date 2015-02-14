package com.enonic.xp.schema.content;

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
