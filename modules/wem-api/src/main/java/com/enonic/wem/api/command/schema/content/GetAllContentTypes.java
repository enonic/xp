package com.enonic.wem.api.command.schema.content;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.content.ContentTypes;

public class GetAllContentTypes
    extends Command<ContentTypes>
{
    @Override
    public void validate()
    {
    }

    private boolean mixinReferencesToFormItems = false;

    public boolean isMixinReferencesToFormItems()
    {
        return mixinReferencesToFormItems;
    }

    public GetAllContentTypes mixinReferencesToFormItems( final boolean value )
    {
        mixinReferencesToFormItems = value;
        return this;
    }

}
