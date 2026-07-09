package com.enonic.xp.core.impl.schema;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.CreateNamespaceParams;
import com.enonic.xp.app.Namespace;
import com.enonic.xp.app.UpdateNamespaceParams;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.schema.CreateCmsParams;
import com.enonic.xp.schema.CreateComponentParams;
import com.enonic.xp.schema.CreateContentSchemaParams;
import com.enonic.xp.schema.CreateMacroParams;
import com.enonic.xp.schema.CreatePhrasesParams;
import com.enonic.xp.schema.DeleteMacroParams;
import com.enonic.xp.schema.DeletePhrasesParams;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.UpdateCmsParams;
import com.enonic.xp.schema.UpdateComponentParams;
import com.enonic.xp.schema.UpdateContentSchemaParams;
import com.enonic.xp.schema.UpdateMacroParams;
import com.enonic.xp.schema.UpdatePhrasesParams;
import com.enonic.xp.schema.UpdateStylesParams;
import com.enonic.xp.schema.CreateStylesParams;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;

public interface SchemaAuditLogSupport
{
    void createComponent( CreateComponentParams params, ComponentType type, SchemaResult<?> result );

    void updateComponent( UpdateComponentParams params, ComponentType type, SchemaResult<?> result );

    void deleteComponent( DescriptorKey key, ComponentType type );

    void createContentSchema( CreateContentSchemaParams params, ContentSchemaType type, SchemaResult<?> result );

    void updateContentSchema( UpdateContentSchemaParams params, ContentSchemaType type, SchemaResult<?> result );

    void deleteContentSchema( BaseSchemaName name, ContentSchemaType type );

    void createCms( CreateCmsParams params, SchemaResult<CmsDescriptor> result );

    void updateCms( UpdateCmsParams params, SchemaResult<CmsDescriptor> result );

    void deleteCms( ApplicationKey key );

    void createStyles( CreateStylesParams params, SchemaResult<StyleDescriptor> result );

    void updateStyles( UpdateStylesParams params, SchemaResult<StyleDescriptor> result );

    void deleteStyles( ApplicationKey key );

    void createMacro( CreateMacroParams params, SchemaResult<MacroDescriptor> result );

    void updateMacro( UpdateMacroParams params, SchemaResult<MacroDescriptor> result );

    void deleteMacro( DeleteMacroParams params );

    void createPhrases( CreatePhrasesParams params, Resource result );

    void updatePhrases( UpdatePhrasesParams params, Resource result );

    void deletePhrases( DeletePhrasesParams params );

    void createNamespace( CreateNamespaceParams params, Namespace result );

    void updateNamespace( UpdateNamespaceParams params, Namespace result );

    void deleteNamespace( ApplicationKey key );
}
