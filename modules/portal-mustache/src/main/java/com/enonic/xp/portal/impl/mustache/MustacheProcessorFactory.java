package com.enonic.xp.portal.impl.mustache;

import com.samskivert.mustache.Mustache;

final class MustacheProcessorFactory
{
    private final Mustache.Compiler compiler;

    public MustacheProcessorFactory()
    {
        this.compiler = Mustache.compiler();
    }

    public MustacheProcessor newProcessor()
    {
        return new MustacheProcessor( this.compiler );
    }
}
