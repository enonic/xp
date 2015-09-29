package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.enonic.xp.content.Content;

public class NewContentPublishItem
{
    private final String id;

    private final String path;

    private final String iconUrl;

    private final String displayName;

    private final String compareStatus;

    private final String name;

    private final String type;

    private final boolean valid;

    public NewContentPublishItem( Builder builder )
    {
        this.id = builder.id;
        this.path = builder.path;
        this.compareStatus = builder.compareStatus;
        this.displayName = builder.displayName;
        this.iconUrl = builder.iconUrl;
        this.name = builder.name;
        this.type = builder.type;
        this.valid = builder.valid;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @SuppressWarnings("unused")
    public String getId()
    {
        return id;
    }

    @SuppressWarnings("unused")
    public String getPath()
    {
        return path;
    }

    @SuppressWarnings("unused")
    public String getCompareStatus()
    {
        return compareStatus;
    }

    @SuppressWarnings("unused")
    public String getIconUrl()
    {
        return iconUrl;
    }

    @SuppressWarnings("unused")
    public String getDisplayName()
    {
        return displayName;
    }

    @SuppressWarnings("unused")
    public String getName()
    {
        return name;
    }

    @SuppressWarnings("unused")
    public String getType()
    {
        return type;
    }

    @SuppressWarnings("unused")
    public boolean isValid()
    {
        return valid;
    }

    public static class Builder
    {

        protected String id;

        protected String path;

        protected String displayName;

        protected String name;

        protected String type;

        protected boolean valid;

        protected Content content;

        protected String compareStatus;

        protected String iconUrl;

        public Builder content( final Content resolvedContent )
        {
            this.content = resolvedContent;
            return this;
        }

        public Builder compareStatus( final String compareStatus )
        {
            this.compareStatus = compareStatus;
            return this;
        }

        public Builder iconUrl( final String iconUrl )
        {
            this.iconUrl = iconUrl;
            return this;
        }

        public NewContentPublishItem build()
        {
            this.id = content.getId().toString();
            this.path = content.getPath().toString();
            this.displayName = content.getDisplayName();
            this.name = content.getName().toString();
            this.type = content.getType().toString();
            this.valid = content.isValid();

            return new NewContentPublishItem( this );
        }
    }


}
