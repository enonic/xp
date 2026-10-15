package com.enonic.xp.core.impl.app;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.CreateOrUpdateCmsParams;
import com.enonic.xp.resource.CreateOrUpdateComponentParams;
import com.enonic.xp.resource.CreateOrUpdateContentSchemaParams;
import com.enonic.xp.resource.CreateOrUpdateMacroParams;
import com.enonic.xp.resource.CreateOrUpdatePhrasesParams;
import com.enonic.xp.resource.CreateOrUpdateStylesParams;
import com.enonic.xp.resource.DeleteDynamicPhrasesParams;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;

/**
 * One audit log entry per call of the dynamic schema service. A create or update entry carries the resource written, when one
 * is given, and what became of the icon: the mime type and size of an icon set, or {@code iconRemoved} when it is removed.
 */
public interface DynamicSchemaAuditLogSupport
{
    void createComponent( CreateOrUpdateComponentParams params, DynamicComponentType type, DynamicSchemaResult<?> result );

    void updateComponent( CreateOrUpdateComponentParams params, DynamicComponentType type, DynamicSchemaResult<?> result );

    void deleteComponent( DescriptorKey key, DynamicComponentType type );

    void createContentSchema( CreateOrUpdateContentSchemaParams params, DynamicContentSchemaType type, DynamicSchemaResult<?> result );

    void updateContentSchema( CreateOrUpdateContentSchemaParams params, DynamicContentSchemaType type, DynamicSchemaResult<?> result );

    void deleteContentSchema( BaseSchemaName name, DynamicContentSchemaType type );

    void createCms( CreateOrUpdateCmsParams params, DynamicSchemaResult<CmsDescriptor> result );

    void updateCms( CreateOrUpdateCmsParams params, DynamicSchemaResult<CmsDescriptor> result );

    void deleteCms( ApplicationKey key );

    void createStyles( CreateOrUpdateStylesParams params, DynamicSchemaResult<StyleDescriptor> result );

    void updateStyles( CreateOrUpdateStylesParams params, DynamicSchemaResult<StyleDescriptor> result );

    void deleteStyles( ApplicationKey key );

    void createMacro( CreateOrUpdateMacroParams params, DynamicSchemaResult<MacroDescriptor> result );

    void updateMacro( CreateOrUpdateMacroParams params, DynamicSchemaResult<MacroDescriptor> result );

    void deleteMacro( MacroKey key );

    /**
     * @param iconMimeType mime type of the icon set along with the descriptor, {@code null} when no icon is set
     */
    void createApplicationDescriptor( ApplicationKey key, String resource, String iconMimeType, long iconSize );

    /**
     * @param iconMimeType mime type of the icon set along with the descriptor, {@code null} when the icon is left as is or removed
     * @param iconRemoved  {@code true} when the icon is removed along with the update
     */
    void updateApplicationDescriptor( ApplicationKey key, String resource, String iconMimeType, long iconSize, boolean iconRemoved );

    void createPhrases( CreateOrUpdatePhrasesParams params, Resource result );

    void updatePhrases( CreateOrUpdatePhrasesParams params, Resource result );

    void deletePhrases( DeleteDynamicPhrasesParams params );
}