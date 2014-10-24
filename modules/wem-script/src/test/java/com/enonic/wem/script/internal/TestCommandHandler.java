package com.enonic.wem.script.internal;

import com.enonic.wem.script.command.CommandHandler;

public final class TestCommandHandler
    implements CommandHandler<TestCommand>
{
    @Override
    public Class<TestCommand> getType()
    {
        return TestCommand.class;
    }

    @Override
    public TestCommand newCommand()
    {
        return new TestCommand();
    }

    @Override
    public void invoke( final TestCommand command )
    {
        final String result = command.getTransform().apply( command.getName() ).toString();
        command.setResult( result );
    }
}
