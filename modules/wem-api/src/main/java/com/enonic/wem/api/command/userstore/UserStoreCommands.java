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

    public UpdateUserStore update()
    {
        return new UpdateUserStore();
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
