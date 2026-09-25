package com.enonic.xp.resource;

import java.util.List;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;

public interface DynamicSchemaService
{
    DynamicSchemaResult<PartDescriptor> createPart( CreateDynamicComponentParams params );

    DynamicSchemaResult<PartDescriptor> updatePart( UpdateDynamicComponentParams params );

    DynamicSchemaResult<PartDescriptor> getPart( DescriptorKey key );

    List<DynamicSchemaResult<PartDescriptor>> listParts( ApplicationKey key );

    boolean deletePart( DescriptorKey key );

    Icon setPartIcon( SetDynamicComponentIconParams params );

    Icon getPartIcon( DescriptorKey key );

    boolean deletePartIcon( DescriptorKey key );

    DynamicSchemaResult<LayoutDescriptor> createLayout( CreateDynamicComponentParams params );

    DynamicSchemaResult<LayoutDescriptor> updateLayout( UpdateDynamicComponentParams params );

    DynamicSchemaResult<LayoutDescriptor> getLayout( DescriptorKey key );

    List<DynamicSchemaResult<LayoutDescriptor>> listLayouts( ApplicationKey key );

    boolean deleteLayout( DescriptorKey key );

    DynamicSchemaResult<PageDescriptor> createPage( CreateDynamicComponentParams params );

    DynamicSchemaResult<PageDescriptor> updatePage( UpdateDynamicComponentParams params );

    DynamicSchemaResult<PageDescriptor> getPage( DescriptorKey key );

    List<DynamicSchemaResult<PageDescriptor>> listPages( ApplicationKey key );

    boolean deletePage( DescriptorKey key );

    DynamicSchemaResult<ContentType> createContentType( CreateDynamicContentSchemaParams params );

    DynamicSchemaResult<ContentType> updateContentType( UpdateDynamicContentSchemaParams params );

    DynamicSchemaResult<ContentType> getContentType( ContentTypeName name );

    List<DynamicSchemaResult<ContentType>> listContentTypes( ApplicationKey key );

    boolean deleteContentType( ContentTypeName name );

    Icon setContentTypeIcon( SetDynamicContentSchemaIconParams params );

    Icon getContentTypeIcon( ContentTypeName name );

    boolean deleteContentTypeIcon( ContentTypeName name );

    DynamicSchemaResult<FormFragmentDescriptor> createFormFragment( CreateDynamicContentSchemaParams params );

    DynamicSchemaResult<FormFragmentDescriptor> updateFormFragment( UpdateDynamicContentSchemaParams params );

    DynamicSchemaResult<FormFragmentDescriptor> getFormFragment( FormFragmentName name );

    List<DynamicSchemaResult<FormFragmentDescriptor>> listFormFragments( ApplicationKey key );

    boolean deleteFormFragment( FormFragmentName name );

    Icon setFormFragmentIcon( SetDynamicContentSchemaIconParams params );

    Icon getFormFragmentIcon( FormFragmentName name );

    boolean deleteFormFragmentIcon( FormFragmentName name );

    DynamicSchemaResult<MixinDescriptor> createMixin( CreateDynamicContentSchemaParams params );

    DynamicSchemaResult<MixinDescriptor> updateMixin( UpdateDynamicContentSchemaParams params );

    DynamicSchemaResult<MixinDescriptor> getMixin( MixinName name );

    List<DynamicSchemaResult<MixinDescriptor>> listMixins( ApplicationKey key );

    boolean deleteMixin( MixinName name );

    Icon setMixinIcon( SetDynamicContentSchemaIconParams params );

    Icon getMixinIcon( MixinName name );

    boolean deleteMixinIcon( MixinName name );

    DynamicSchemaResult<CmsDescriptor> updateCms( UpdateDynamicCmsParams params );

    DynamicSchemaResult<CmsDescriptor> getCmsDescriptor( ApplicationKey key );

    DynamicSchemaResult<StyleDescriptor> createStyles( CreateDynamicStylesParams params );

    DynamicSchemaResult<StyleDescriptor> updateStyles( UpdateDynamicStylesParams params );

    DynamicSchemaResult<StyleDescriptor> getStyles( ApplicationKey key );

    boolean deleteStyles( ApplicationKey key );

    DynamicSchemaResult<MacroDescriptor> createMacro( CreateDynamicMacroParams params );

    DynamicSchemaResult<MacroDescriptor> updateMacro( UpdateDynamicMacroParams params );

    DynamicSchemaResult<MacroDescriptor> getMacro( MacroKey key );

    List<DynamicSchemaResult<MacroDescriptor>> listMacros( ApplicationKey key );

    boolean deleteMacro( MacroKey key );

    Icon setMacroIcon( SetDynamicMacroIconParams params );

    Icon getMacroIcon( MacroKey key );

    boolean deleteMacroIcon( MacroKey key );

    Resource createPhrases( CreateDynamicPhrasesParams params );

    Resource updatePhrases( UpdateDynamicPhrasesParams params );

    Resource getPhrases( GetDynamicPhrasesParams params );

    List<Resource> listPhrases( ApplicationKey key );

    boolean deletePhrases( DeleteDynamicPhrasesParams params );
}
