package com.enonic.wem.api.command.schema.content;

public class GetAllContentTypesParams
{
    private boolean mixinReferencesToFormItems = false;

    public boolean isMixinReferencesToFormItems()
    {
        return mixinReferencesToFormItems;
    }

    public GetAllContentTypesParams mixinReferencesToFormItems( final boolean value )
    {
        mixinReferencesToFormItems = value;
        return this;
    }

}
