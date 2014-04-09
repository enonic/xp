package com.enonic.wem.api.command;

import com.enonic.wem.api.command.content.site.SiteCommands;
import com.enonic.wem.api.command.relationship.RelationshipCommands;
import com.enonic.wem.api.command.schema.SchemaCommands;

public final class Commands
{
    private static final SiteCommands SITE_COMMANDS = new SiteCommands();

    private static final RelationshipCommands RELATIONSHIP_COMMANDS = new RelationshipCommands();

    private static final SchemaCommands SCHEMA_COMMANDS = new SchemaCommands();

    private Commands()
    {
    }

    public static SiteCommands site()
    {
        return SITE_COMMANDS;
    }

    public static RelationshipCommands relationship()
    {
        return RELATIONSHIP_COMMANDS;
    }

    public static SchemaCommands schema()
    {
        return SCHEMA_COMMANDS;
    }

}
