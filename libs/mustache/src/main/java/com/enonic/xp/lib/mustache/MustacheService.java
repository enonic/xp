package com.enonic.xp.lib.mustache;

import com.samskivert.mustache.Mustache;

public final class MustacheService
{
    private final Mustache.Compiler compiler;

    public MustacheService()
    {
        this.compiler = Mustache.compiler();
    }

    public MustacheProcessor newProcessor()
    {
        return new MustacheProcessor(this.compiler);
    }
}
