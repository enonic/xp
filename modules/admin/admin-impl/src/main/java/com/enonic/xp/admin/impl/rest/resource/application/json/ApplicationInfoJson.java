package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.google.common.base.Preconditions;

import com.enonic.xp.admin.impl.json.content.page.PageDescriptorListJson;
import com.enonic.xp.admin.impl.json.content.page.region.LayoutDescriptorsJson;
import com.enonic.xp.admin.impl.json.content.page.region.PartDescriptorsJson;
import com.enonic.xp.admin.impl.json.schema.content.ContentTypeSummaryListJson;
import com.enonic.xp.admin.impl.json.schema.relationship.RelationshipTypeListJson;
import com.enonic.xp.admin.impl.rest.resource.macro.MacroIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.macro.json.MacrosJson;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.relationship.RelationshipTypeIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.tool.json.AdminToolDescriptorsJson;
import com.enonic.xp.admin.impl.rest.resource.widget.json.WidgetDescriptorsJson;
import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.app.ApplicationInfo;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.region.PartDescriptors;

public class ApplicationInfoJson
{
    private ContentTypeSummaryListJson contentTypes;

    private PageDescriptorListJson pages;

    private PartDescriptorsJson parts;

    private LayoutDescriptorsJson layouts;

    private RelationshipTypeListJson relations;

    private ContentReferencesJson references;

    private MacrosJson macros;

    private ApplicationTaskDescriptorsJson tasks;

    private WidgetDescriptorsJson widgets;

    private AdminToolDescriptorsJson tools;

    private ApplicationIdProviderJson idProvider;

    private ApplicationDeploymentJson deployment;

    private ApplicationInfoJson( final Builder builder )
    {
        this.contentTypes = new ContentTypeSummaryListJson( builder.applicationInfo.getContentTypes(), builder.contentTypeIconUrlResolver,
                                                            builder.localeMessageResolver );
        this.pages =
            new PageDescriptorListJson( PageDescriptors.from( builder.applicationInfo.getPages() ), builder.localeMessageResolver );
        this.parts = new PartDescriptorsJson( PartDescriptors.from( builder.applicationInfo.getParts() ), builder.localeMessageResolver );
        this.layouts =
            new LayoutDescriptorsJson( LayoutDescriptors.from( builder.applicationInfo.getLayouts() ), builder.localeMessageResolver );
        this.relations = new RelationshipTypeListJson( builder.applicationInfo.getRelations(), builder.relationshipTypeIconUrlResolver );
        this.references = new ContentReferencesJson( builder.applicationInfo.getContentReferences() );
        this.macros = new MacrosJson( builder.applicationInfo.getMacros(), builder.macroIconUrlResolver, builder.localeMessageResolver );
        this.tasks = new ApplicationTaskDescriptorsJson( builder.applicationInfo.getTasks() );
        this.widgets = new WidgetDescriptorsJson( builder.widgetDescriptors );
        this.tools = builder.adminToolDescriptors;
        this.idProvider =
            new ApplicationIdProviderJson( builder.applicationInfo.getAuthDescriptor(), builder.applicationInfo.getUserStoreReferences() );
        this.deployment = new ApplicationDeploymentJson( builder.deploymentUrl );
    }

    public ContentTypeSummaryListJson getContentTypes()
    {
        return contentTypes;
    }

    public PageDescriptorListJson getPages()
    {
        return pages;
    }

    public PartDescriptorsJson getParts()
    {
        return parts;
    }

    public LayoutDescriptorsJson getLayouts()
    {
        return layouts;
    }

    public RelationshipTypeListJson getRelations()
    {
        return relations;
    }

    public MacrosJson getMacros()
    {
        return macros;
    }

    public ContentReferencesJson getReferences()
    {
        return references;
    }

    public ApplicationTaskDescriptorsJson getTasks()
    {
        return tasks;
    }

    public ApplicationIdProviderJson getIdProvider()
    {
        return idProvider;
    }

    public ApplicationDeploymentJson getDeployment()
    {
        return deployment;
    }

    public WidgetDescriptorsJson getWidgets()
    {
        return widgets;
    }

    public AdminToolDescriptorsJson getTools()
    {
        return tools;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ApplicationInfo applicationInfo;

        private Descriptors<WidgetDescriptor> widgetDescriptors;

        private AdminToolDescriptorsJson adminToolDescriptors;

        private String deploymentUrl;

        private RelationshipTypeIconUrlResolver relationshipTypeIconUrlResolver;

        private MacroIconUrlResolver macroIconUrlResolver;

        private ContentTypeIconUrlResolver contentTypeIconUrlResolver;

        private LocaleMessageResolver localeMessageResolver;

        private Builder()
        {
        }

        public Builder setApplicationInfo( final ApplicationInfo applicationInfo )
        {
            this.applicationInfo = applicationInfo;
            return this;
        }

        public Builder setWidgetDescriptors( final Descriptors<WidgetDescriptor> widgetDescriptors )
        {
            this.widgetDescriptors = widgetDescriptors;
            return this;
        }

        public Builder setAdminToolDescriptors( final AdminToolDescriptorsJson adminToolDescriptors )
        {
            this.adminToolDescriptors = adminToolDescriptors;
            return this;
        }

        public Builder setDeploymentUrl( final String deploymentUrl )
        {
            this.deploymentUrl = deploymentUrl;
            return this;
        }

        public Builder setRelationshipTypeIconUrlResolver( final RelationshipTypeIconUrlResolver relationshipTypeIconUrlResolver )
        {
            this.relationshipTypeIconUrlResolver = relationshipTypeIconUrlResolver;
            return this;
        }

        public Builder setMacroIconUrlResolver( final MacroIconUrlResolver macroIconUrlResolver )
        {
            this.macroIconUrlResolver = macroIconUrlResolver;
            return this;
        }

        public Builder setContentTypeIconUrlResolver( final ContentTypeIconUrlResolver contentTypeIconUrlResolver )
        {
            this.contentTypeIconUrlResolver = contentTypeIconUrlResolver;
            return this;
        }

        public Builder setLocaleMessageResolver( final LocaleMessageResolver localeMessageResolver )
        {
            this.localeMessageResolver = localeMessageResolver;
            return this;
        }

        public void validate()
        {
            Preconditions.checkNotNull( this.applicationInfo, "applicationInfo cannot be null" );
            Preconditions.checkNotNull( this.relationshipTypeIconUrlResolver, "relationshipTypeIconUrlResolver cannot be null" );
            Preconditions.checkNotNull( this.macroIconUrlResolver, "macroIconUrlResolver cannot be null" );
            Preconditions.checkNotNull( this.contentTypeIconUrlResolver, "contentTypeIconUrlResolver cannot be null" );
        }

        public ApplicationInfoJson build()
        {
            validate();
            return new ApplicationInfoJson( this );
        }
    }
}
