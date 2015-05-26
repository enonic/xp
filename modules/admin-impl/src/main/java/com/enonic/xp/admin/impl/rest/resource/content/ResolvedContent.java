package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;

public class ResolvedContent
{
    private final String id;

    private final String path;

    private final String iconUrl;

    private final String displayName;

    private final String compareStatus;

    private final String name;

    private final String type;

    private final boolean isValid;

    public ResolvedContent( Builder builder )
    {
        this.id = builder.id;
        this.path = builder.path;
        this.compareStatus = builder.compareStatus;
        this.displayName = builder.displayName;
        this.iconUrl = builder.iconUrl;
        this.name = builder.name;
        this.type = builder.type;
        this.isValid = builder.isValid;
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
        return isValid;
    }

    public static class Builder
    {

        protected String id;

        protected String path;

        protected String displayName;

        protected String name;

        protected String type;

        protected boolean isValid;

        protected Content resolvedContent;

        protected String compareStatus;

        protected String iconUrl;

        public Builder resolvedContent( final Content resolvedContent )
        {
            this.resolvedContent = resolvedContent;
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

        public ResolvedContent build()
        {
            this.id = resolvedContent.getId().toString();
            this.path = resolvedContent.getPath().toString();
            this.displayName = resolvedContent.getDisplayName();
            this.name = resolvedContent.getName().toString();
            this.type = resolvedContent.getType().toString();
            this.isValid = resolvedContent.isValid();

            return new ResolvedContent( this );
        }
    }

    public static class ResolvedRequestedContent
        extends ResolvedContent
    {

        private final int childrenCount;

        private final int dependantsCount;

        public ResolvedRequestedContent( Builder builder )
        {
            super( builder );
            this.childrenCount = builder.childrenCount;
            this.dependantsCount = builder.dependantsCount;
        }

        public static Builder create()
        {
            return new Builder();
        }

        @SuppressWarnings("unused")
        public int getChildrenCount()
        {
            return childrenCount;
        }

        @SuppressWarnings("unused")
        public int getDependantsCount()
        {
            return dependantsCount;
        }

        public static final class Builder
            extends ResolvedContent.Builder
        {

            private int childrenCount;

            private int dependantsCount;

            public Builder childrenCount( final int childrenCount )
            {
                this.childrenCount = childrenCount;
                return this;
            }

            public Builder dependantsCount( final int dependantsCount )
            {
                this.dependantsCount = dependantsCount;
                return this;
            }

            public Builder resolvedContent( final Content resolvedContent )
            {
                this.resolvedContent = resolvedContent;
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

            public ResolvedRequestedContent build()
            {
                this.id = resolvedContent.getId().toString();
                this.path = resolvedContent.getPath().toString();
                this.displayName = resolvedContent.getDisplayName();
                this.name = resolvedContent.getName().toString();
                this.type = resolvedContent.getType().toString();
                this.isValid = resolvedContent.isValid();

                return new ResolvedRequestedContent( this );
            }
        }
    }

    public static class ResolvedDependantContent
        extends ResolvedContent
    {

        private final String dependsOnContentId;

        public ResolvedDependantContent( Builder builder )
        {
            super( builder );
            this.dependsOnContentId = builder.dependsOnContentId;
        }

        public static Builder create()
        {
            return new Builder();
        }

        @SuppressWarnings("unused")
        public String getDependsOnContentId()
        {
            return dependsOnContentId;
        }

        public static final class Builder
            extends ResolvedContent.Builder
        {

            private String dependsOnContentId;

            public Builder dependsOnContentId( final ContentId dependsOnContentId )
            {
                if ( dependsOnContentId != null )
                {
                    this.dependsOnContentId = dependsOnContentId.toString();
                }
                return this;
            }

            public Builder resolvedContent( final Content resolvedContent )
            {
                this.resolvedContent = resolvedContent;
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

            public ResolvedDependantContent build()
            {
                this.id = resolvedContent.getId().toString();
                this.path = resolvedContent.getPath().toString();
                this.displayName = resolvedContent.getDisplayName();
                this.name = resolvedContent.getName().toString();
                this.type = resolvedContent.getType().toString();
                this.isValid = resolvedContent.isValid();

                return new ResolvedDependantContent( this );
            }
        }
    }

}
