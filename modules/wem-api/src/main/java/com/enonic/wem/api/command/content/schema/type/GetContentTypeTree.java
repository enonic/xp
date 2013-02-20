package com.enonic.wem.api.command.content.schema.type;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.schema.type.ContentType;
import com.enonic.wem.api.support.tree.Tree;

public class GetContentTypeTree
    extends Command<Tree<ContentType>>
{
    @Override
    public void validate()
    {
    }
}
