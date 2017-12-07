package com.enonic.xp.app;

import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.content.Contents;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.region.PartDescriptors;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.relationship.RelationshipTypes;
import com.enonic.xp.security.UserStores;
import com.enonic.xp.task.TaskDescriptor;


public final class ApplicationInfo
{
    private ContentTypes contentTypes;

    private PageDescriptors pages;

    private PartDescriptors parts;

    private LayoutDescriptors layouts;

    private RelationshipTypes relations;

    private MacroDescriptors macros;

    private Descriptors<TaskDescriptor> tasks;

    private Contents contentReferences;

    private UserStores userStoreReferences;

    private AuthDescriptor authDescriptor;

    private ApplicationInfo( final Builder builder )
    {

        this.contentTypes = builder.contentTypes;
        this.pages = builder.pages;
        this.parts = builder.parts;
        this.layouts = builder.layouts;
        this.relations = builder.relations;
        this.macros = builder.macros;
        this.tasks = builder.tasks;
        this.contentReferences = builder.contentReferences;
        this.userStoreReferences = builder.userStoreReferences;
        this.authDescriptor = builder.authDescriptor;
    }

    public ContentTypes getContentTypes()
    {
        return contentTypes;
    }

    public PageDescriptors getPages()
    {
        return pages;
    }

    public PartDescriptors getParts()
    {
        return parts;
    }

    public LayoutDescriptors getLayouts()
    {
        return layouts;
    }

    public RelationshipTypes getRelations()
    {
        return relations;
    }

    public MacroDescriptors getMacros()
    {
        return macros;
    }

    public Descriptors<TaskDescriptor> getTasks()
    {
        return tasks;
    }

    public Contents getContentReferences()
    {
        return contentReferences;
    }

    public UserStores getUserStoreReferences()
    {
        return userStoreReferences;
    }

    public AuthDescriptor getAuthDescriptor()
    {
        return authDescriptor;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private ContentTypes contentTypes;

        private PageDescriptors pages;

        private PartDescriptors parts;

        private LayoutDescriptors layouts;

        private RelationshipTypes relations;

        private MacroDescriptors macros;

        private Descriptors<TaskDescriptor> tasks;

        private Contents contentReferences;

        private UserStores userStoreReferences;

        private AuthDescriptor authDescriptor;

        private Builder()
        {
        }

        public Builder setContentTypes( final ContentTypes contentTypes )
        {
            this.contentTypes = contentTypes;
            return this;
        }

        public Builder setPages( final PageDescriptors pages )
        {
            this.pages = pages;
            return this;
        }

        public Builder setParts( final PartDescriptors parts )
        {
            this.parts = parts;
            return this;
        }

        public Builder setLayouts( final LayoutDescriptors layouts )
        {
            this.layouts = layouts;
            return this;
        }

        public Builder setRelations( final RelationshipTypes relations )
        {
            this.relations = relations;
            return this;
        }

        public Builder setMacros( final MacroDescriptors macros )
        {
            this.macros = macros;
            return this;
        }

        public Builder setTasks( final Descriptors<TaskDescriptor> tasks )
        {
            this.tasks = tasks;
            return this;
        }

        public Builder setContentReferences( final Contents contentReferences )
        {
            this.contentReferences = contentReferences;
            return this;
        }

        public Builder setUserStoreReferences( final UserStores userStoreReferences )
        {
            this.userStoreReferences = userStoreReferences;
            return this;
        }

        public Builder setAuthDescriptor( final AuthDescriptor authDescriptor )
        {
            this.authDescriptor = authDescriptor;
            return this;
        }

        public ApplicationInfo build()
        {
            return new ApplicationInfo( this );
        }
    }
}
