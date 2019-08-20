package com.enonic.xp.admin.impl.json.content;

import com.enonic.xp.admin.impl.json.content.attachment.AttachmentJson;
import com.enonic.xp.content.Content;
import com.enonic.xp.layer.ContentLayer;

@SuppressWarnings("UnusedDeclaration")
public class ContentInLayerJson
    extends ContentIdJson
{
    private final Content content;

    private final ContentLayer layer;

    public ContentInLayerJson( final Content content, final ContentLayer layer )
    {
        super( content.getId() );
        this.content = content;
        this.layer = layer;
    }

    public String getPath()
    {
        return content.getPath().toString();
    }

    public String getName()
    {
        return content.getName().toString();
    }

    public String getDisplayName()
    {
        return content.getDisplayName();
    }

    public Boolean getInherited()
    {
        return content.getInherited();
    }

    public String getLayer() {
        return layer.getName().toString();
    }

    public String getParentLayer() {
        return layer.getParentName() != null ? layer.getParentName().toString() : null;
    }

    public String getLayerDisplayName() {
        return layer.getDisplayName();
    }

    public AttachmentJson getIcon()
    {
        return layer.getIcon() != null ? new AttachmentJson(layer.getIcon()) : null;
    }

}
