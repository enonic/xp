package com.enonic.xp.admin.impl.rest.resource.layer;

import com.enonic.xp.admin.impl.json.content.attachment.AttachmentJson;
import com.enonic.xp.layer.ContentLayer;

public class ContentLayerJson
{
    private String name;

    private String parentName;

    private String displayName;

    private String description;

    private String language;

    private AttachmentJson icon;

    public ContentLayerJson( final ContentLayer contentLayer )
    {
        this.name = contentLayer.getName() == null ? null : contentLayer.getName().getValue();
        this.parentName = contentLayer.getParentName() == null ? null : contentLayer.getParentName().getValue();
        this.displayName = contentLayer.getDisplayName();
        this.description = contentLayer.getDescription();
        this.language = contentLayer.getLanguage() == null ? null : contentLayer.getLanguage().toLanguageTag();
        this.icon = contentLayer.getIcon() == null ? null : new AttachmentJson( contentLayer.getIcon() );
    }

    public String getName()
    {
        return name;
    }

    public String getParentName()
    {
        return parentName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public String getLanguage()
    {
        return language;
    }

    public AttachmentJson getIcon()
    {
        return icon;
    }
}
