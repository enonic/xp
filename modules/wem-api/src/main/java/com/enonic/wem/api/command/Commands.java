package com.enonic.wem.api.command;

import com.enonic.wem.api.command.account.AccountCommands;
import com.enonic.wem.api.command.content.ContentCommands;
import com.enonic.wem.api.command.content.attachment.AttachmentCommands;
import com.enonic.wem.api.command.content.binary.BinaryCommands;
import com.enonic.wem.api.command.content.page.PageCommands;
import com.enonic.wem.api.command.content.site.SiteCommands;
import com.enonic.wem.api.command.entity.NodeCommands;
import com.enonic.wem.api.command.module.ModuleCommands;
import com.enonic.wem.api.command.relationship.RelationshipCommands;
import com.enonic.wem.api.command.resource.ResourceCommands;
import com.enonic.wem.api.command.schema.SchemaCommands;
import com.enonic.wem.api.command.schema.content.ContentTypeCommands;
import com.enonic.wem.api.command.schema.mixin.MixinCommands;
import com.enonic.wem.api.command.schema.relationship.RelationshipTypeCommands;
import com.enonic.wem.api.command.space.SpaceCommands;
import com.enonic.wem.api.command.userstore.UserStoreCommands;

public final class Commands
{
    private static final NodeCommands NODE_COMMANDS = new NodeCommands();

    private static final SpaceCommands SPACE_COMMANDS = new SpaceCommands();

    private static final AccountCommands ACCOUNT_COMMANDS = new AccountCommands();

    private static final UserStoreCommands USER_STORE_COMMANDS = new UserStoreCommands();

    private static final ContentCommands CONTENT_COMMANDS = new ContentCommands();

    private static final SiteCommands SITE_COMMANDS = new SiteCommands();

    private static final PageCommands PAGE_COMMANDS = new PageCommands();

    private static final RelationshipCommands RELATIONSHIP_COMMANDS = new RelationshipCommands();

    private static final SchemaCommands SCHEMA_COMMANDS = new SchemaCommands();

    private static final ContentTypeCommands CONTENT_TYPE_COMMANDS = new ContentTypeCommands();

    private static final MixinCommands MIXIN_COMMANDS = new MixinCommands();

    private static final RelationshipTypeCommands RELATIONSHIP_TYPE_COMMANDS = new RelationshipTypeCommands();

    private static final BinaryCommands BINARY_COMMANDS = new BinaryCommands();

    private static final AttachmentCommands ATTACHMENT_COMMANDS = new AttachmentCommands();

    private static final ResourceCommands RESOURCE_COMMANDS = new ResourceCommands();

    private static final ModuleCommands MODULE_COMMANDS = new ModuleCommands();

    private Commands()
    {
    }

    public static NodeCommands node()
    {
        return NODE_COMMANDS;
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

    public static PageCommands page()
    {
        return PAGE_COMMANDS;
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

    public static BinaryCommands binary()
    {
        return BINARY_COMMANDS;
    }

    public static AttachmentCommands attachment()
    {
        return ATTACHMENT_COMMANDS;
    }

    public static ResourceCommands resource()
    {
        return RESOURCE_COMMANDS;
    }

    public static ModuleCommands module()
    {
        return MODULE_COMMANDS;
    }

}
