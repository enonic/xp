package com.enonic.xp.core.impl.app;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.CreateNamespaceParams;
import com.enonic.xp.app.Namespace;
import com.enonic.xp.app.UpdateNamespaceParams;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.resource.CreateDynamicCmsParams;
import com.enonic.xp.resource.CreateDynamicComponentParams;
import com.enonic.xp.resource.CreateDynamicContentSchemaParams;
import com.enonic.xp.resource.CreateDynamicMacroParams;
import com.enonic.xp.resource.CreateDynamicPhrasesParams;
import com.enonic.xp.resource.DeleteDynamicComponentParams;
import com.enonic.xp.resource.DeleteDynamicContentSchemaParams;
import com.enonic.xp.resource.DeleteDynamicMacroParams;
import com.enonic.xp.resource.DeleteDynamicPhrasesParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.UpdateDynamicCmsParams;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.resource.UpdateDynamicMacroParams;
import com.enonic.xp.resource.UpdateDynamicPhrasesParams;
import com.enonic.xp.resource.UpdateDynamicStylesParams;
import com.enonic.xp.resource.CreateDynamicStylesParams;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;

public interface SchemaAuditLogSupport
{
    void createComponent( CreateDynamicComponentParams params, DynamicSchemaResult<?> result );

    void updateComponent( UpdateDynamicComponentParams params, DynamicSchemaResult<?> result );

    void deleteComponent( DeleteDynamicComponentParams params );

    void createContentSchema( CreateDynamicContentSchemaParams params, DynamicSchemaResult<?> result );

    void updateContentSchema( UpdateDynamicContentSchemaParams params, DynamicSchemaResult<?> result );

    void deleteContentSchema( DeleteDynamicContentSchemaParams params );

    void createCms( CreateDynamicCmsParams params, DynamicSchemaResult<CmsDescriptor> result );

    void updateCms( UpdateDynamicCmsParams params, DynamicSchemaResult<CmsDescriptor> result );

    void deleteCms( ApplicationKey key );

    void createStyles( CreateDynamicStylesParams params, DynamicSchemaResult<StyleDescriptor> result );

    void updateStyles( UpdateDynamicStylesParams params, DynamicSchemaResult<StyleDescriptor> result );

    void deleteStyles( ApplicationKey key );

    void createMacro( CreateDynamicMacroParams params, DynamicSchemaResult<MacroDescriptor> result );

    void updateMacro( UpdateDynamicMacroParams params, DynamicSchemaResult<MacroDescriptor> result );

    void deleteMacro( DeleteDynamicMacroParams params );

    void createPhrases( CreateDynamicPhrasesParams params, Resource result );

    void updatePhrases( UpdateDynamicPhrasesParams params, Resource result );

    void deletePhrases( DeleteDynamicPhrasesParams params );

    void createNamespace( CreateNamespaceParams params, Namespace result );

    void updateNamespace( UpdateNamespaceParams params, Namespace result );

    void deleteNamespace( ApplicationKey key );
}
