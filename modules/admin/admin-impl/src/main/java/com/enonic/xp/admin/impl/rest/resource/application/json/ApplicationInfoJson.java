package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.enonic.xp.admin.impl.json.content.page.PageDescriptorListJson;
import com.enonic.xp.admin.impl.json.content.page.region.LayoutDescriptorsJson;
import com.enonic.xp.admin.impl.json.content.page.region.PartDescriptorsJson;
import com.enonic.xp.admin.impl.json.schema.content.ContentTypeSummaryListJson;
import com.enonic.xp.admin.impl.json.schema.relationship.RelationshipTypeListJson;
import com.enonic.xp.admin.impl.rest.resource.macro.json.MacrosJson;

public class ApplicationInfoJson
{
    private ContentTypeSummaryListJson contentTypes;

    private PageDescriptorListJson pages;

    private PartDescriptorsJson parts;

    private LayoutDescriptorsJson layouts;

    private RelationshipTypeListJson relations;

    private ContentReferencesJson references;

    private MacrosJson macros;

    private ApplicationDeploymentJson deployment;

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

    public ApplicationDeploymentJson getDeployment()
    {
        return deployment;
    }

    public ApplicationInfoJson setContentTypes( final ContentTypeSummaryListJson contentTypes )
    {
        this.contentTypes = contentTypes;
        return this;
    }

    public ApplicationInfoJson setPages( final PageDescriptorListJson pages )
    {
        this.pages = pages;
        return this;
    }

    public ApplicationInfoJson setParts( final PartDescriptorsJson parts )
    {
        this.parts = parts;
        return this;
    }

    public ApplicationInfoJson setLayouts( final LayoutDescriptorsJson layouts )
    {
        this.layouts = layouts;
        return this;
    }

    public ApplicationInfoJson setRelations( final RelationshipTypeListJson relations )
    {
        this.relations = relations;
        return this;
    }

    public ApplicationInfoJson setReferences( final ContentReferencesJson references )
    {
        this.references = references;
        return this;
    }

    public ApplicationInfoJson setMacros( final MacrosJson macros )
    {
        this.macros = macros;
        return this;
    }

    public ApplicationInfoJson setDeployment( final ApplicationDeploymentJson deployment )
    {
        this.deployment = deployment;
        return this;
    }
}
