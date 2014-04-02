package com.enonic.wem.api.command;

import com.enonic.wem.api.command.content.ContentCommands;
import com.enonic.wem.api.command.content.blob.BlobCommands;
import com.enonic.wem.api.command.content.site.SiteCommands;
import com.enonic.wem.api.command.module.ModuleCommands;
import com.enonic.wem.api.command.relationship.RelationshipCommands;
import com.enonic.wem.api.command.resource.ResourceCommands;
import com.enonic.wem.api.command.schema.SchemaCommands;
import com.enonic.wem.api.command.schema.content.ContentTypeCommands;
import com.enonic.wem.api.command.schema.relationship.RelationshipTypeCommands;
import com.enonic.wem.api.content.page.PageCommands;

public final class Commands
{
    private static final ContentCommands CONTENT_COMMANDS = new ContentCommands();

    private static final SiteCommands SITE_COMMANDS = new SiteCommands();

    private static final PageCommands PAGE_COMMANDS = new PageCommands();

    private static final RelationshipCommands RELATIONSHIP_COMMANDS = new RelationshipCommands();

    private static final SchemaCommands SCHEMA_COMMANDS = new SchemaCommands();

    private static final ContentTypeCommands CONTENT_TYPE_COMMANDS = new ContentTypeCommands();

    private static final RelationshipTypeCommands RELATIONSHIP_TYPE_COMMANDS = new RelationshipTypeCommands();

    private static final BlobCommands BLOB_COMMANDS = new BlobCommands();

    private static final ResourceCommands RESOURCE_COMMANDS = new ResourceCommands();

    private static final ModuleCommands MODULE_COMMANDS = new ModuleCommands();

    private Commands()
    {
    }

    public static ContentCommands content()
    {
        return CONTENT_COMMANDS;
    }

    public static SiteCommands site()
    {
        return SITE_COMMANDS;
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

    public static RelationshipTypeCommands relationshipType()
    {
        return RELATIONSHIP_TYPE_COMMANDS;
    }

    public static BlobCommands blob()
    {
        return BLOB_COMMANDS;
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
