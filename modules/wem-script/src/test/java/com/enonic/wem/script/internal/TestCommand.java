package com.enonic.wem.script.internal;

import java.util.function.Function;

import com.enonic.wem.script.command.Command;
import com.enonic.wem.script.command.CommandName;

@CommandName("test.command")
public final class TestCommand
    extends Command<String>
{
    private String name;

    private Function<String, Object> transform;

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public Function<String, Object> getTransform()
    {
        return transform;
    }

    public void setTransform( final Function<String, Object> transform )
    {
        this.transform = transform;
    }
}
