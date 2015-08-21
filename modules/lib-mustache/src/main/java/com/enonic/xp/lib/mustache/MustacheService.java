package com.enonic.xp.lib.mustache;

import com.samskivert.mustache.Mustache;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.resource.ResourceService;

public final class MustacheService
    implements ScriptBean
{
    private final Mustache.Compiler compiler;

    private ResourceService resourceService;

    public MustacheService()
    {
        this.compiler = Mustache.compiler();
    }

    public MustacheProcessor newProcessor()
    {
        MustacheProcessor processor = new MustacheProcessor( this.compiler );
        processor.setResourceService( resourceService );
        return processor;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.resourceService = context.getService( ResourceService.class ).get();
    }
}
