package com.enonic.xp.app;

import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.region.PartDescriptors;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.relationship.RelationshipTypes;
import com.enonic.xp.security.IdProviders;
import com.enonic.xp.task.TaskDescriptor;


public final class ApplicationInfo
{
    private final ContentTypes contentTypes;

    private final PageDescriptors pages;

    private final PartDescriptors parts;

    private final LayoutDescriptors layouts;

    private final RelationshipTypes relations;

    private final MacroDescriptors macros;

    private final Descriptors<TaskDescriptor> tasks;

    private final IdProviders idProviderReferences;

    private final IdProviderDescriptor idProviderDescriptor;

    private ApplicationInfo( final Builder builder )
    {

        this.contentTypes = builder.contentTypes;
        this.pages = builder.pages;
        this.parts = builder.parts;
        this.layouts = builder.layouts;
        this.relations = builder.relations;
        this.macros = builder.macros;
        this.tasks = builder.tasks;
        this.idProviderReferences = builder.idProviderReferences;
        this.idProviderDescriptor = builder.idProviderDescriptor;
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

    public IdProviders getIdProviderReferences()
    {
        return idProviderReferences;
    }

    public IdProviderDescriptor getIdProviderDescriptor()
    {
        return idProviderDescriptor;
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

        private IdProviders idProviderReferences;

        private IdProviderDescriptor idProviderDescriptor;

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

        public Builder setIdProviderReferences( final IdProviders idProviderReferences )
        {
            this.idProviderReferences = idProviderReferences;
            return this;
        }

        public Builder setIdProviderDescriptor( final IdProviderDescriptor idProviderDescriptor )
        {
            this.idProviderDescriptor = idProviderDescriptor;
            return this;
        }

        public ApplicationInfo build()
        {
            return new ApplicationInfo( this );
        }
    }
}
