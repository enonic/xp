package com.enonic.xp.admin.impl.rest.resource.project.layer.json;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.json.content.attachment.AttachmentJson;
import com.enonic.xp.project.layer.ContentLayer;
import com.enonic.xp.project.layer.ContentLayerKey;

public final class ContentLayerJson
{
    private String key;

    private List<String> parentKeys;

    private String displayName;

    private String description;

    private String locale;

    private AttachmentJson icon;

    public ContentLayerJson( final ContentLayer contentLayer )
    {
        this.key = contentLayer.getKey() != null ? contentLayer.getKey().toString() : null;
        this.parentKeys = contentLayer.getParentKeys() != null ? contentLayer.getParentKeys().stream().
            map( ContentLayerKey::toString ).
            collect( Collectors.toList() ) : null;
        this.displayName = contentLayer.getDisplayName();
        this.description = contentLayer.getDescription();
        this.locale = contentLayer.getLocale() != null ? contentLayer.getLocale().toLanguageTag() : null;
        this.icon = contentLayer.getIcon() != null ? new AttachmentJson( contentLayer.getIcon() ) : null;
    }

    public String getKey()
    {
        return key;
    }

    public List<String> getParentKeys()
    {
        return parentKeys;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public String getLocale()
    {
        return locale;
    }

    public AttachmentJson getIcon()
    {
        return icon;
    }
}
