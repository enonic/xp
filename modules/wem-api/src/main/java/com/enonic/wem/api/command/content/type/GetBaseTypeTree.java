package com.enonic.wem.api.command.content.type;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.type.BaseType;
import com.enonic.wem.api.support.tree.Tree;

public class GetBaseTypeTree
    extends Command<Tree<BaseType>>
{
    @Override
    public void validate()
    {
    }
}
