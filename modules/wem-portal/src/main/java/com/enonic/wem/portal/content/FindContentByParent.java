package com.enonic.wem.portal.content;

import com.enonic.wem.api.content.Contents;
import com.enonic.wem.script.command.Command;

public final class FindContentByParent
    extends Command<Contents>
{
    private int from;

    private int size;

    private String parentPath;

    public FindContentByParent()
    {
        this.from = 0;
        this.size = 100;
    }

    public int getFrom()
    {
        return from;
    }

    public void setFrom( final int from )
    {
        this.from = from;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize( final int size )
    {
        this.size = size;
    }

    public String getParentPath()
    {
        return parentPath;
    }

    public void setParentPath( final String parentPath )
    {
        this.parentPath = parentPath;
    }
}
