package com.enonic.wem.api.command;

import com.enonic.wem.api.command.account.AccountCommands;

public abstract class Commands
{
    public static AccountCommands account()
    {
        return new AccountCommands();
    }
}
