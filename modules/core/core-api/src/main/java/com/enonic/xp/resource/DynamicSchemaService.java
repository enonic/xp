package com.enonic.xp.resource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.site.SiteDescriptor;

public interface DynamicSchemaService
{
    ComponentDescriptor createComponent( CreateDynamicComponentParams params );

    ComponentDescriptor updateComponent( UpdateDynamicComponentParams params );

    ComponentDescriptor getComponent( GetDynamicComponentParams params );

    boolean deleteComponent( DeleteDynamicComponentParams params );

    BaseSchema<?> createContentSchema( CreateDynamicContentSchemaParams params );

    BaseSchema<?> updateContentSchema( UpdateDynamicContentSchemaParams params );

    BaseSchema<?> getContentSchema( GetDynamicContentSchemaParams params );

    boolean deleteContentSchema( DeleteDynamicContentSchemaParams params );

    SiteDescriptor updateSite( UpdateDynamicSiteParams params );

    SiteDescriptor getSite( ApplicationKey key );

}
