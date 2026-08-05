package com.enonic.xp.resource;

import java.util.List;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.CreateNamespaceParams;
import com.enonic.xp.app.Namespace;
import com.enonic.xp.app.UpdateNamespaceParams;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;

public interface SchemaService
{
    ApplicationKeys listApplicationKeys();

    Namespace createNamespace( CreateNamespaceParams params );

    Namespace updateNamespace( UpdateNamespaceParams params );

    boolean deleteNamespace( ApplicationKey key );

    Namespace getNamespace( ApplicationKey key );

    List<Namespace> listNamespaces();

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

    DynamicSchemaResult<CmsDescriptor> createCms( CreateDynamicCmsParams params );

    DynamicSchemaResult<CmsDescriptor> updateCms( UpdateDynamicCmsParams params );

    DynamicSchemaResult<CmsDescriptor> getCmsDescriptor( ApplicationKey key );

    boolean deleteCms( ApplicationKey key );

    Resource createPhrases( CreateDynamicPhrasesParams params );

    Resource updatePhrases( UpdateDynamicPhrasesParams params );

    Resource getPhrases( GetDynamicPhrasesParams params );

    List<Resource> listPhrases( ApplicationKey key );

    boolean deletePhrases( DeleteDynamicPhrasesParams params );

    DynamicSchemaResult<StyleDescriptor> createStyles( CreateDynamicStylesParams params );

    DynamicSchemaResult<StyleDescriptor> updateStyles( UpdateDynamicStylesParams params );

    DynamicSchemaResult<StyleDescriptor> getStyles( ApplicationKey key );

    boolean deleteStyles( ApplicationKey key );

    DynamicSchemaResult<MacroDescriptor> createMacro( CreateDynamicMacroParams params );

    DynamicSchemaResult<MacroDescriptor> updateMacro( UpdateDynamicMacroParams params );

    DynamicSchemaResult<MacroDescriptor> getMacro( GetDynamicMacroParams params );

    List<DynamicSchemaResult<MacroDescriptor>> listMacros( ListDynamicMacrosParams params );

    boolean deleteMacro( DeleteDynamicMacroParams params );

}
