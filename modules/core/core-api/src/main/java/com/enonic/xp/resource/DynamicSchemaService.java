package com.enonic.xp.resource;

import java.util.List;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;

public interface DynamicSchemaService
{
    <T extends ComponentDescriptor> DynamicSchemaResult<T> createComponent( CreateDynamicComponentParams params );

    <T extends ComponentDescriptor> DynamicSchemaResult<T> updateComponent( UpdateDynamicComponentParams params );

    <T extends ComponentDescriptor> DynamicSchemaResult<T> getComponent( GetDynamicComponentParams params );

    <T extends ComponentDescriptor> List<DynamicSchemaResult<T>> listComponents( ListDynamicComponentsParams params );

    boolean deleteComponent( DeleteDynamicComponentParams params );

    <T extends BaseSchema<?>> DynamicSchemaResult<T> createContentSchema( CreateDynamicContentSchemaParams params );

    <T extends BaseSchema<?>> DynamicSchemaResult<T> updateContentSchema( UpdateDynamicContentSchemaParams params );

    <T extends BaseSchema<?>> DynamicSchemaResult<T> getContentSchema( GetDynamicContentSchemaParams params );

    <T extends BaseSchema<?>> List<DynamicSchemaResult<T>> listContentSchemas( ListDynamicContentSchemasParams params );

    boolean deleteContentSchema( DeleteDynamicContentSchemaParams params );

    DynamicSchemaResult<CmsDescriptor> updateCms( UpdateDynamicCmsParams params );

    DynamicSchemaResult<CmsDescriptor> getCmsDescriptor( ApplicationKey key );

    DynamicSchemaResult<StyleDescriptor> createStyles( CreateDynamicStylesParams params );

    DynamicSchemaResult<StyleDescriptor> updateStyles( UpdateDynamicStylesParams params );

    DynamicSchemaResult<StyleDescriptor> getStyles( ApplicationKey key );

    boolean deleteStyles( ApplicationKey key );


}
