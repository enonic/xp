package com.enonic.xp.resource;

import java.util.List;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.schema.BaseSchema;
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
    /**
     * @deprecated use the method of the kind of the component, {@link #createPart}, {@link #createLayout} or {@link #createPage},
     * which this dispatches to by the {@code type} of the params
     */
    @Deprecated
    <T extends ComponentDescriptor> DynamicSchemaResult<T> createComponent( CreateDynamicComponentParams params );

    /**
     * @deprecated use {@link #updatePart}, {@link #updateLayout} or {@link #updatePage}
     */
    @Deprecated
    <T extends ComponentDescriptor> DynamicSchemaResult<T> updateComponent( UpdateDynamicComponentParams params );

    /**
     * @deprecated use {@link #getPart}, {@link #getLayout} or {@link #getPage}
     */
    @Deprecated
    <T extends ComponentDescriptor> DynamicSchemaResult<T> getComponent( GetDynamicComponentParams params );

    /**
     * @deprecated use {@link #listParts}, {@link #listLayouts} or {@link #listPages}
     */
    @Deprecated
    <T extends ComponentDescriptor> List<DynamicSchemaResult<T>> listComponents( ListDynamicComponentsParams params );

    /**
     * @deprecated use {@link #deletePart}, {@link #deleteLayout} or {@link #deletePage}
     */
    @Deprecated
    boolean deleteComponent( DeleteDynamicComponentParams params );

    /**
     * @deprecated use the method of the kind of the schema, {@link #createContentType}, {@link #createFormFragment} or
     * {@link #createMixin}, which this dispatches to by the {@code type} of the params
     */
    @Deprecated
    <T extends BaseSchema<?>> DynamicSchemaResult<T> createContentSchema( CreateDynamicContentSchemaParams params );

    /**
     * @deprecated use {@link #updateContentType}, {@link #updateFormFragment} or {@link #updateMixin}
     */
    @Deprecated
    <T extends BaseSchema<?>> DynamicSchemaResult<T> updateContentSchema( UpdateDynamicContentSchemaParams params );

    /**
     * @deprecated use {@link #getContentType}, {@link #getFormFragment} or {@link #getMixin}
     */
    @Deprecated
    <T extends BaseSchema<?>> DynamicSchemaResult<T> getContentSchema( GetDynamicContentSchemaParams params );

    /**
     * @deprecated use {@link #listContentTypes}, {@link #listFormFragments} or {@link #listMixins}
     */
    @Deprecated
    <T extends BaseSchema<?>> List<DynamicSchemaResult<T>> listContentSchemas( ListDynamicContentSchemasParams params );

    /**
     * @deprecated use {@link #deleteContentType}, {@link #deleteFormFragment} or {@link #deleteMixin}
     */
    @Deprecated
    boolean deleteContentSchema( DeleteDynamicContentSchemaParams params );

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

    /**
     * Creates the cms descriptor of the application, or updates it in place when there is one.
     */
    DynamicSchemaResult<CmsDescriptor> createOrUpdateCms( CreateOrUpdateCmsParams params );

    /**
     * @deprecated use {@link #createOrUpdateCms(CreateOrUpdateCmsParams)}, which this delegates to
     */
    @Deprecated
    DynamicSchemaResult<CmsDescriptor> updateCms( UpdateDynamicCmsParams params );

    DynamicSchemaResult<CmsDescriptor> getCmsDescriptor( ApplicationKey key );

    boolean deleteCms( ApplicationKey key );

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
