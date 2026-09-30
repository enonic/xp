package com.enonic.xp.schema;

public final class SchemaNodePropertyNames
{
    public static final String RESOURCE = "resource";

    public static final String MIME_TYPE = "mimeType";

    public static final String ICON = "icon";

    /**
     * Set on a descriptor node when its icon is set or removed, so that the descriptor resource changes along with the icon.
     */
    public static final String ICON_MODIFIED_TIME = "iconModifiedTime";

    private SchemaNodePropertyNames()
    {
    }
}
