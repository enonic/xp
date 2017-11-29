package com.enonic.xp.admin.impl.rest.resource.application.json;

import com.enonic.xp.admin.impl.json.content.page.PageDescriptorListJson;
import com.enonic.xp.admin.impl.json.content.page.region.LayoutDescriptorsJson;
import com.enonic.xp.admin.impl.json.content.page.region.PartDescriptorsJson;
import com.enonic.xp.admin.impl.json.schema.content.ContentTypeSummaryListJson;
import com.enonic.xp.admin.impl.json.schema.relationship.RelationshipTypeListJson;
import com.enonic.xp.admin.impl.rest.resource.macro.json.MacrosJson;

public class ApplicationInfoJson
{
    private ContentTypeSummaryListJson contentTypesJson;

    private PageDescriptorListJson pagesJson;

    private PartDescriptorsJson partsJson;

    private LayoutDescriptorsJson layoutsJson;

    private RelationshipTypeListJson relationsJson;

    private ContentReferencesJson referencesJson;

    private MacrosJson macrosJson;

    public ContentTypeSummaryListJson getContentTypesJson()
    {
        return contentTypesJson;
    }

    public PageDescriptorListJson getPagesJson()
    {
        return pagesJson;
    }

    public PartDescriptorsJson getPartsJson()
    {
        return partsJson;
    }

    public LayoutDescriptorsJson getLayoutsJson()
    {
        return layoutsJson;
    }

    public RelationshipTypeListJson getRelationsJson()
    {
        return relationsJson;
    }

    public MacrosJson getMacrosJson()
    {
        return macrosJson;
    }

    public ContentReferencesJson getReferencesJson()
    {
        return referencesJson;
    }

    public ApplicationInfoJson setContentTypesJson( final ContentTypeSummaryListJson contentTypesJson )
    {
        this.contentTypesJson = contentTypesJson;
        return this;
    }

    public ApplicationInfoJson setPagesJson( final PageDescriptorListJson pagesJson )
    {
        this.pagesJson = pagesJson;
        return this;
    }

    public ApplicationInfoJson setPartsJson( final PartDescriptorsJson partsJson )
    {
        this.partsJson = partsJson;
        return this;
    }

    public ApplicationInfoJson setLayoutsJson( final LayoutDescriptorsJson layoutsJson )
    {
        this.layoutsJson = layoutsJson;
        return this;
    }

    public ApplicationInfoJson setRelationsJson( final RelationshipTypeListJson relationsJson )
    {
        this.relationsJson = relationsJson;
        return this;
    }

    public ApplicationInfoJson setReferencesJson( final ContentReferencesJson referencesJson )
    {
        this.referencesJson = referencesJson;
        return this;
    }

    public ApplicationInfoJson setMacrosJson( final MacrosJson macrosJson )
    {
        this.macrosJson = macrosJson;
        return this;
    }
}
