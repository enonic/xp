package com.enonic.wem.api.command.content.schema.content;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.support.tree.Tree;

public class GetContentTypeTree
    extends Command<Tree<ContentType>>
{
    @Override
    public void validate()
    {
    }
}
