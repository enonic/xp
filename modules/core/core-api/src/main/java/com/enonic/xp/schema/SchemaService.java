package com.enonic.xp.schema;

import java.util.List;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.CreateNamespaceParams;
import com.enonic.xp.app.Namespace;
import com.enonic.xp.app.UpdateNamespaceParams;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
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

    SchemaResult<PartDescriptor> createPart( CreateComponentParams params );

    SchemaResult<PartDescriptor> updatePart( UpdateComponentParams params );

    SchemaResult<PartDescriptor> getPart( DescriptorKey key );

    List<SchemaResult<PartDescriptor>> listParts( ApplicationKey key );

    boolean deletePart( DescriptorKey key );

    SchemaResult<LayoutDescriptor> createLayout( CreateComponentParams params );

    SchemaResult<LayoutDescriptor> updateLayout( UpdateComponentParams params );

    SchemaResult<LayoutDescriptor> getLayout( DescriptorKey key );

    List<SchemaResult<LayoutDescriptor>> listLayouts( ApplicationKey key );

    boolean deleteLayout( DescriptorKey key );

    SchemaResult<PageDescriptor> createPage( CreateComponentParams params );

    SchemaResult<PageDescriptor> updatePage( UpdateComponentParams params );

    SchemaResult<PageDescriptor> getPage( DescriptorKey key );

    List<SchemaResult<PageDescriptor>> listPages( ApplicationKey key );

    boolean deletePage( DescriptorKey key );

    SchemaResult<ContentType> createContentType( CreateContentSchemaParams params );

    SchemaResult<ContentType> updateContentType( UpdateContentSchemaParams params );

    SchemaResult<ContentType> getContentType( ContentTypeName name );

    List<SchemaResult<ContentType>> listContentTypes( ApplicationKey key );

    boolean deleteContentType( ContentTypeName name );

    SchemaResult<FormFragmentDescriptor> createFormFragment( CreateContentSchemaParams params );

    SchemaResult<FormFragmentDescriptor> updateFormFragment( UpdateContentSchemaParams params );

    SchemaResult<FormFragmentDescriptor> getFormFragment( FormFragmentName name );

    List<SchemaResult<FormFragmentDescriptor>> listFormFragments( ApplicationKey key );

    boolean deleteFormFragment( FormFragmentName name );

    SchemaResult<MixinDescriptor> createMixin( CreateContentSchemaParams params );

    SchemaResult<MixinDescriptor> updateMixin( UpdateContentSchemaParams params );

    SchemaResult<MixinDescriptor> getMixin( MixinName name );

    List<SchemaResult<MixinDescriptor>> listMixins( ApplicationKey key );

    boolean deleteMixin( MixinName name );

    SchemaResult<CmsDescriptor> createCms( CreateCmsParams params );

    SchemaResult<CmsDescriptor> updateCms( UpdateCmsParams params );

    SchemaResult<CmsDescriptor> getCmsDescriptor( ApplicationKey key );

    boolean deleteCms( ApplicationKey key );

    Resource createPhrases( CreatePhrasesParams params );

    Resource updatePhrases( UpdatePhrasesParams params );

    Resource getPhrases( GetPhrasesParams params );

    List<Resource> listPhrases( ApplicationKey key );

    boolean deletePhrases( DeletePhrasesParams params );

    SchemaResult<StyleDescriptor> createStyles( CreateStylesParams params );

    SchemaResult<StyleDescriptor> updateStyles( UpdateStylesParams params );

    SchemaResult<StyleDescriptor> getStyles( ApplicationKey key );

    boolean deleteStyles( ApplicationKey key );

    SchemaResult<MacroDescriptor> createMacro( CreateMacroParams params );

    SchemaResult<MacroDescriptor> updateMacro( UpdateMacroParams params );

    SchemaResult<MacroDescriptor> getMacro( GetMacroParams params );

    List<SchemaResult<MacroDescriptor>> listMacros( ListMacrosParams params );

    boolean deleteMacro( DeleteMacroParams params );

}
