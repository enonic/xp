package com.enonic.wem.api.command;

import com.enonic.wem.api.command.account.AccountCommands;
import com.enonic.wem.api.command.content.ContentCommands;
import com.enonic.wem.api.command.content.relationship.RelationshipCommands;
import com.enonic.wem.api.command.content.schema.SchemaCommands;
import com.enonic.wem.api.command.content.schema.content.ContentTypeCommands;
import com.enonic.wem.api.command.content.schema.mixin.MixinCommands;
import com.enonic.wem.api.command.content.schema.relationshiptype.RelationshipTypeCommands;
import com.enonic.wem.api.command.space.SpaceCommands;
import com.enonic.wem.api.command.userstore.UserStoreCommands;

public final class Commands
{
    private static final SpaceCommands SPACE_COMMANDS = new SpaceCommands();

    private static final AccountCommands ACCOUNT_COMMANDS = new AccountCommands();

    private static final UserStoreCommands USER_STORE_COMMANDS = new UserStoreCommands();

    private static final ContentCommands CONTENT_COMMANDS = new ContentCommands();

    private static final RelationshipCommands RELATIONSHIP_COMMANDS = new RelationshipCommands();

    private static final SchemaCommands SCHEMA_COMMANDS = new SchemaCommands();

    private static final ContentTypeCommands CONTENT_TYPE_COMMANDS = new ContentTypeCommands();

    private static final MixinCommands MIXIN_COMMANDS = new MixinCommands();

    private static final RelationshipTypeCommands RELATIONSHIP_TYPE_COMMANDS = new RelationshipTypeCommands();

    private Commands()
    {
    }

    public static AccountCommands account()
    {
        return ACCOUNT_COMMANDS;
    }

    public static UserStoreCommands userStore()
    {
        return USER_STORE_COMMANDS;
    }

    public static ContentCommands content()
    {
        return CONTENT_COMMANDS;
    }

    public static RelationshipCommands relationship()
    {
        return RELATIONSHIP_COMMANDS;
    }

    public static SchemaCommands schema()
    {
        return SCHEMA_COMMANDS;
    }

    public static ContentTypeCommands contentType()
    {
        return CONTENT_TYPE_COMMANDS;
    }

    public static MixinCommands mixin()
    {
        return MIXIN_COMMANDS;
    }

    public static RelationshipTypeCommands relationshipType()
    {
        return RELATIONSHIP_TYPE_COMMANDS;
    }

    public static SpaceCommands space()
    {
        return SPACE_COMMANDS;
    }
}
