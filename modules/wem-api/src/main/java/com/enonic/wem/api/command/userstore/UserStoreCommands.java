package com.enonic.wem.api.command.userstore;

public final class UserStoreCommands
{
    public GetUserStores get()
    {
        return new GetUserStores();
    }

    public FindAllUserStores findAll()
    {
        return new FindAllUserStores();
    }

    public CreateUserStore create()
    {
        return new CreateUserStore();
    }

    public UpdateUserStores update()
    {
        return new UpdateUserStores();
    }

    public DeleteUserStore delete()
    {
        return new DeleteUserStore();
    }

    public GetUserStoreConnectors getConnectors()
    {
        return new GetUserStoreConnectors();
    }
}
