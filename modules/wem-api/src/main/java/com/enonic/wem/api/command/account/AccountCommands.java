package com.enonic.wem.api.command.account;

public final class AccountCommands
{
    public GetAccounts get()
    {
        return new GetAccounts();
    }

    public FindAccounts find()
    {
        return new FindAccounts();
    }

    public CreateAccount create()
    {
        return new CreateAccount();
    }

    public UpdateAccounts update()
    {
        return new UpdateAccounts();
    }

    public DeleteAccounts delete()
    {
        return new DeleteAccounts();
    }

    public FindMembers findMembers()
    {
        return new FindMembers();
    }

    public FindMemberships findMemberships()
    {
        return new FindMemberships();
    }

    public ChangePassword changePassword()
    {
        return new ChangePassword();
    }

    public ValidatePassword validatePassword()
    {
        return new ValidatePassword();
    }
}
