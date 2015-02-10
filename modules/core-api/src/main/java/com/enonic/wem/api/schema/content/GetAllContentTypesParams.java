package com.enonic.wem.api.schema.content;

public class GetAllContentTypesParams
{
    private boolean inlinesToFormItems = false;

    public boolean isInlinesToFormItems()
    {
        return inlinesToFormItems;
    }

    public GetAllContentTypesParams inlinesToFormItems( final boolean value )
    {
        inlinesToFormItems = value;
        return this;
    }

}
