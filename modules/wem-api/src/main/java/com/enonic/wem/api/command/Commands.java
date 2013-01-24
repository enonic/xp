package com.enonic.wem.api.command;

import com.enonic.wem.api.command.account.AccountCommands;
import com.enonic.wem.api.command.content.ContentCommands;
import com.enonic.wem.api.command.content.relationship.RelationshipTypeCommands;
import com.enonic.wem.api.command.content.space.SpaceCommands;
import com.enonic.wem.api.command.content.type.BaseTypeCommands;
import com.enonic.wem.api.command.content.type.ContentTypeCommands;
import com.enonic.wem.api.command.content.type.MixinCommands;
import com.enonic.wem.api.command.userstore.UserStoreCommands;

public abstract class Commands
{
    public static AccountCommands account()
    {
        return new AccountCommands();
    }

    public static UserStoreCommands userStore()
    {
        return new UserStoreCommands();
    }

    public static ContentCommands content()
    {
        return new ContentCommands();
    }

    public static BaseTypeCommands baseType()
    {
        return new BaseTypeCommands();
    }

    public static ContentTypeCommands contentType()
    {
        return new ContentTypeCommands();
    }

    public static MixinCommands mixin()
    {
        return new MixinCommands();
    }

    public static RelationshipTypeCommands relationshipType()
    {
        return new RelationshipTypeCommands();
    }

    public static SpaceCommands space()
    {
        return new SpaceCommands();
    }
}
