package com.enonic.wem.api.command.account;

public final class AccountCommands
{
    public ChangePassword changePassword()
    {
        return new ChangePassword();
    }

    public ValidatePassword validatePassword()
    {
        return new ValidatePassword();
    }
}
