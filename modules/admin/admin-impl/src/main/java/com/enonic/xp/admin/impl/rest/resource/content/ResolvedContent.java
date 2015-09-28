package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.content.Content;

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

        public ResolvedContent build()
        {
            this.id = content.getId().toString();
            this.path = content.getPath().toString();
            this.displayName = content.getDisplayName();
            this.name = content.getName().toString();
            this.type = content.getType().toString();
            this.isValid = content.isValid();

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

            public Builder content( final Content content )
            {
                this.content = content;
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
                this.id = content.getId().toString();
                this.path = content.getPath().toString();
                this.displayName = content.getDisplayName();
                this.name = content.getName().toString();
                this.type = content.getType().toString();
                this.isValid = content.isValid();

                return new ResolvedRequestedContent( this );
            }
        }
    }

    public static class ResolvedDependencyContent
        extends ResolvedContent
    {

        private final boolean child;

        public ResolvedDependencyContent( Builder builder )
        {
            super( builder );
            this.child = builder.child;
        }

        public static Builder create()
        {
            return new Builder();
        }

        @SuppressWarnings("unused")
        public boolean isChild()
        {
            return child;
        }

        public static final class Builder
            extends ResolvedContent.Builder
        {
            private boolean child;

            public Builder isChild( final boolean child )
            {
                this.child = child;
                return this;
            }

            public Builder content( final Content content )
            {
                this.content = content;
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

            public ResolvedDependencyContent build()
            {
                this.id = content.getId().toString();
                this.path = content.getPath().toString();
                this.displayName = content.getDisplayName();
                this.name = content.getName().toString();
                this.type = content.getType().toString();
                this.isValid = content.isValid();

                return new ResolvedDependencyContent( this );
            }
        }
    }

}