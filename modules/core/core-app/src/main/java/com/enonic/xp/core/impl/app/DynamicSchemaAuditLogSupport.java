package com.enonic.xp.core.impl.app;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.CreateDynamicComponentParams;
import com.enonic.xp.resource.CreateDynamicContentSchemaParams;
import com.enonic.xp.resource.CreateDynamicMacroParams;
import com.enonic.xp.resource.CreateDynamicPhrasesParams;
import com.enonic.xp.resource.CreateDynamicStylesParams;
import com.enonic.xp.resource.DeleteDynamicPhrasesParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.UpdateDynamicCmsParams;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.resource.UpdateDynamicMacroParams;
import com.enonic.xp.resource.UpdateDynamicPhrasesParams;
import com.enonic.xp.resource.UpdateDynamicStylesParams;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;

public interface DynamicSchemaAuditLogSupport
{
    void createComponent( CreateDynamicComponentParams params, DynamicComponentType type, DynamicSchemaResult<?> result );

    void updateComponent( UpdateDynamicComponentParams params, DynamicComponentType type, DynamicSchemaResult<?> result );

    void deleteComponent( DescriptorKey key, DynamicComponentType type );

    void setComponentIcon( DescriptorKey key, DynamicComponentType type, String mimeType, long size );

    void deleteComponentIcon( DescriptorKey key, DynamicComponentType type );

    void createContentSchema( CreateDynamicContentSchemaParams params, DynamicContentSchemaType type, DynamicSchemaResult<?> result );

    void updateContentSchema( UpdateDynamicContentSchemaParams params, DynamicContentSchemaType type, DynamicSchemaResult<?> result );

    void deleteContentSchema( BaseSchemaName name, DynamicContentSchemaType type );

    void setContentSchemaIcon( BaseSchemaName name, DynamicContentSchemaType type, String mimeType, long size );

    void deleteContentSchemaIcon( BaseSchemaName name, DynamicContentSchemaType type );

    void createCms( CreateDynamicCmsParams params, DynamicSchemaResult<CmsDescriptor> result );

    void updateCms( UpdateDynamicCmsParams params, DynamicSchemaResult<CmsDescriptor> result );

    void deleteCms( ApplicationKey key );

    void createStyles( CreateDynamicStylesParams params, DynamicSchemaResult<StyleDescriptor> result );

    void updateStyles( UpdateDynamicStylesParams params, DynamicSchemaResult<StyleDescriptor> result );

    void deleteStyles( ApplicationKey key );

    void createMacro( CreateDynamicMacroParams params, DynamicSchemaResult<MacroDescriptor> result );

    void updateMacro( UpdateDynamicMacroParams params, DynamicSchemaResult<MacroDescriptor> result );

    void deleteMacro( MacroKey key );

    void setMacroIcon( MacroKey key, String mimeType, long size );

    void deleteMacroIcon( MacroKey key );

    void createPhrases( CreateDynamicPhrasesParams params, Resource result );

    void updatePhrases( UpdateDynamicPhrasesParams params, Resource result );

    void deletePhrases( DeleteDynamicPhrasesParams params );
}
