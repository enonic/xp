package com.enonic.xp.resource;

import java.util.List;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.style.StyleDescriptor;

public interface DynamicSchemaService
{
    DynamicSchemaResult<ComponentDescriptor> createComponent( CreateDynamicComponentParams params );

    DynamicSchemaResult<ComponentDescriptor> updateComponent( UpdateDynamicComponentParams params );

    DynamicSchemaResult<ComponentDescriptor> getComponent( GetDynamicComponentParams params );

    List<DynamicSchemaResult<ComponentDescriptor>> listComponents( ListDynamicComponentsParams params );

    boolean deleteComponent( DeleteDynamicComponentParams params );

    DynamicSchemaResult<BaseSchema<?>> createContentSchema( CreateDynamicContentSchemaParams params );

    DynamicSchemaResult<BaseSchema<?>> updateContentSchema( UpdateDynamicContentSchemaParams params );

    DynamicSchemaResult<BaseSchema<?>> getContentSchema( GetDynamicContentSchemaParams params );

    List<DynamicSchemaResult<BaseSchema<?>>> listSchemas( ListDynamicSchemasParams params );

    boolean deleteContentSchema( DeleteDynamicContentSchemaParams params );

    DynamicSchemaResult<SiteDescriptor> updateSite( UpdateDynamicSiteParams params );

    DynamicSchemaResult<SiteDescriptor> getSite( ApplicationKey key );

    DynamicSchemaResult<StyleDescriptor> createStyles( CreateDynamicStylesParams params );

    DynamicSchemaResult<StyleDescriptor> updateStyles( UpdateDynamicStylesParams params );

    DynamicSchemaResult<StyleDescriptor> getStyles( ApplicationKey key );

    boolean deleteStyles( ApplicationKey key );


}
