package com.enonic.wem.api.command.schema.content;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.support.tree.Tree;

public class GetContentTypeTree
    extends Command<Tree<ContentType>>
{
    @Override
    public void validate()
    {
    }
}
