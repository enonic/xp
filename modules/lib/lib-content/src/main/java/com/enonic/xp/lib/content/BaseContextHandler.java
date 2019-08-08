package com.enonic.xp.lib.content;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public abstract class BaseContextHandler
    implements ScriptBean
{
    protected ContentService contentService;

    public final Object execute()
    {
        return this.doExecute();
    }

    protected abstract Object doExecute();

    protected <T> T valueOrDefault( final T value, final T defValue )
    {
        return value == null ? defValue : value;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.contentService = context.getService( ContentService.class ).get();
    }
}
