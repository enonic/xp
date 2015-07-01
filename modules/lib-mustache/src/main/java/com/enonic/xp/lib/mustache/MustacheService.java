package com.enonic.xp.lib.mustache;

import com.samskivert.mustache.Mustache;

import com.enonic.xp.portal.bean.BeanContext;
import com.enonic.xp.portal.bean.ScriptBean;

public final class MustacheService
    implements ScriptBean
{
    private final Mustache.Compiler compiler;

    public MustacheService()
    {
        this.compiler = Mustache.compiler();
    }

    public MustacheProcessor newProcessor()
    {
        return new MustacheProcessor( this.compiler );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        // Do nothing
    }
}
