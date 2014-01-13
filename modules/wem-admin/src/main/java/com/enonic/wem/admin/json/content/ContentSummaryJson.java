package com.enonic.wem.admin.json.content;

import org.joda.time.DateTime;

import com.enonic.wem.admin.json.ChangeTraceableJson;
import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.rest.resource.content.ContentImageIconUrlResolver;
import com.enonic.wem.api.content.Content;

@SuppressWarnings("UnusedDeclaration")
public class ContentSummaryJson
    extends ContentIdJson
    implements ChangeTraceableJson, ItemJson
{
    private final Content content;

    private final String iconUrl;

    private final boolean editable;

    private final boolean deletable;

    private final boolean isSite;

    private final boolean isPage;


    public ContentSummaryJson( Content content )
    {
        super( content.getId() );
        this.content = content;
        this.iconUrl = ContentImageIconUrlResolver.resolve( content );
        this.isSite = content.isSite();
        this.isPage = content.isPage();
        this.editable = ( !this.content.isEmbedded() );
        this.deletable = !this.content.hasChildren() && ( !this.content.isEmbedded() );
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public String getPath()
    {
        return content.getPath().toString();
    }

    public String getName()
    {
        return content.getName().toString();
    }

    public String getType()
    {
        return content.getType() != null ? content.getType().toString() : null;
    }

    public String getDisplayName()
    {
        return content.getDisplayName();
    }

    public String getOwner()
    {
        return content.getOwner() != null ? content.getOwner().toString() : null;
    }

    public boolean getIsRoot()
    {
        return content.isRoot();
    }

    public DateTime getCreatedTime()
    {
        return content.getCreatedTime();
    }

    public String getCreator()
    {
        return content.getCreator() != null ? content.getCreator().toString() : null;
    }

    public DateTime getModifiedTime()
    {
        return content.getModifiedTime();
    }

    public String getModifier()
    {
        return content.getModifier() != null ? content.getModifier().toString() : null;
    }

    public boolean getHasChildren()
    {
        return content.hasChildren();
    }

    public boolean getIsDraft() {
        return content.isDraft();
    }

    public boolean getIsSite()
    {
        return isSite;
    }

    public boolean getIsPage()
    {
        return isPage;
    }

    @Override
    public boolean getEditable()
    {
        return editable;
    }

    @Override
    public boolean getDeletable()
    {
        return deletable;
    }

}
