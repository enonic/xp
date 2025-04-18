package com.enonic.xp.lib.node;

import java.util.Arrays;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.script.ScriptValue;

public class PatchNodeHandlerParams
{
    private NodeKey key;

    private Branches branches;

    private ScriptValue editor;

    public NodeKey getKey()
    {
        return key;
    }

    public void setKey( final String key )
    {
        this.key = NodeKey.from( key );
    }

    public Branches getBranches()
    {
        return branches;
    }

    public void setBranches( final String[] branches )
    {
        this.branches = Arrays.stream( branches ).map( Branch::from ).collect( Branches.collecting() );
    }

    public ScriptValue getEditor()
    {
        return editor;
    }

    public void setEditor( final ScriptValue editor )
    {
        this.editor = editor;
    }
}
