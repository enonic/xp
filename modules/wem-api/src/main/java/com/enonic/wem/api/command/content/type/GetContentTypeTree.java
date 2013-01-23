package com.enonic.wem.api.command.content.type;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.support.tree.Tree;

public class GetContentTypeTree
    extends Command<Tree<ContentType>>
{
    @Override
    public void validate()
    {
    }
}
