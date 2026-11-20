package com.enonic.xp.resource;

import java.util.List;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
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

/**
 * The schema of an application persisted in system-repo: content types, form fragments, mixins, parts, layouts, pages, the cms
 * descriptor, styles, macros and i18n phrases. Each kind is created or updated by a single method, which stores the descriptor
 * together with its icon for the kinds that have one.
 * <p>
 * Apart from the cms descriptor, a schema is written for an application owning a schema only, the one whose cms descriptor is
 * persisted: shipped by its bundle on install, or created through {@link #createOrUpdateCms}.
 */
public interface DynamicSchemaService
{
    /**
     * @deprecated use the method of the kind of the component, {@link #createOrUpdatePart}, {@link #createOrUpdateLayout} or
     * {@link #createOrUpdatePage}, which this dispatches to by the {@code type} of the params
     */
    @Deprecated
    <T extends ComponentDescriptor> DynamicSchemaResult<T> createComponent( CreateDynamicComponentParams params );

    /**
     * @deprecated use {@link #createOrUpdatePart}, {@link #createOrUpdateLayout} or {@link #createOrUpdatePage}
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
     * @deprecated use the method of the kind of the schema, {@link #createOrUpdateContentType}, {@link #createOrUpdateFormFragment}
     * or {@link #createOrUpdateMixin}, which this dispatches to by the {@code type} of the params
     */
    @Deprecated
    <T extends BaseSchema<?>> DynamicSchemaResult<T> createContentSchema( CreateDynamicContentSchemaParams params );

    /**
     * @deprecated use {@link #createOrUpdateContentType}, {@link #createOrUpdateFormFragment} or {@link #createOrUpdateMixin}
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

    /**
     * Creates the part, or updates it when there is one, with its icon.
     */
    DynamicSchemaResult<PartDescriptor> createOrUpdatePart( CreateOrUpdateComponentParams params );

    DynamicSchemaResult<PartDescriptor> getPart( DescriptorKey key );

    List<DynamicSchemaResult<PartDescriptor>> listParts( ApplicationKey key );

    boolean deletePart( DescriptorKey key );

    /**
     * Creates the layout, or updates it when there is one. A layout has no icon.
     */
    DynamicSchemaResult<LayoutDescriptor> createOrUpdateLayout( CreateOrUpdateComponentParams params );

    DynamicSchemaResult<LayoutDescriptor> getLayout( DescriptorKey key );

    List<DynamicSchemaResult<LayoutDescriptor>> listLayouts( ApplicationKey key );

    boolean deleteLayout( DescriptorKey key );

    /**
     * Creates the page, or updates it when there is one. A page has no icon.
     */
    DynamicSchemaResult<PageDescriptor> createOrUpdatePage( CreateOrUpdateComponentParams params );

    DynamicSchemaResult<PageDescriptor> getPage( DescriptorKey key );

    List<DynamicSchemaResult<PageDescriptor>> listPages( ApplicationKey key );

    boolean deletePage( DescriptorKey key );

    /**
     * Creates the content type, or updates it when there is one, with its icon.
     */
    DynamicSchemaResult<ContentType> createOrUpdateContentType( CreateOrUpdateContentSchemaParams params );

    DynamicSchemaResult<ContentType> getContentType( ContentTypeName name );

    List<DynamicSchemaResult<ContentType>> listContentTypes( ApplicationKey key );

    boolean deleteContentType( ContentTypeName name );

    /**
     * Creates the form fragment, or updates it when there is one, with its icon.
     */
    DynamicSchemaResult<FormFragmentDescriptor> createOrUpdateFormFragment( CreateOrUpdateContentSchemaParams params );

    DynamicSchemaResult<FormFragmentDescriptor> getFormFragment( FormFragmentName name );

    List<DynamicSchemaResult<FormFragmentDescriptor>> listFormFragments( ApplicationKey key );

    boolean deleteFormFragment( FormFragmentName name );

    /**
     * Creates the mixin, or updates it when there is one, with its icon.
     */
    DynamicSchemaResult<MixinDescriptor> createOrUpdateMixin( CreateOrUpdateContentSchemaParams params );

    DynamicSchemaResult<MixinDescriptor> getMixin( MixinName name );

    List<DynamicSchemaResult<MixinDescriptor>> listMixins( ApplicationKey key );

    boolean deleteMixin( MixinName name );

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

    /**
     * Creates the style descriptor of the application, or updates it when there is one.
     */
    DynamicSchemaResult<StyleDescriptor> createOrUpdateStyles( CreateOrUpdateStylesParams params );

    /**
     * @deprecated use {@link #createOrUpdateStyles(CreateOrUpdateStylesParams)}, which this delegates to
     */
    @Deprecated
    DynamicSchemaResult<StyleDescriptor> createStyles( CreateDynamicStylesParams params );

    /**
     * @deprecated use {@link #createOrUpdateStyles(CreateOrUpdateStylesParams)}, which this delegates to
     */
    @Deprecated
    DynamicSchemaResult<StyleDescriptor> updateStyles( UpdateDynamicStylesParams params );

    DynamicSchemaResult<StyleDescriptor> getStyles( ApplicationKey key );

    boolean deleteStyles( ApplicationKey key );

    /**
     * Creates the macro, or updates it when there is one, with its icon.
     */
    DynamicSchemaResult<MacroDescriptor> createOrUpdateMacro( CreateOrUpdateMacroParams params );

    DynamicSchemaResult<MacroDescriptor> getMacro( MacroKey key );

    List<DynamicSchemaResult<MacroDescriptor>> listMacros( ApplicationKey key );

    boolean deleteMacro( MacroKey key );

    /**
     * Creates the phrases resource, or updates it when there is one.
     */
    Resource createOrUpdatePhrases( CreateOrUpdatePhrasesParams params );

    Resource getPhrases( GetDynamicPhrasesParams params );

    List<Resource> listPhrases( ApplicationKey key );

    boolean deletePhrases( DeleteDynamicPhrasesParams params );
}