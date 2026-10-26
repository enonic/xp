package com.enonic.xp.app;

import org.jspecify.annotations.NonNull;

import com.enonic.xp.icon.Icon;
import com.enonic.xp.schema.LocalizedText;
import com.enonic.xp.util.GenericValue;

import static java.util.Objects.requireNonNull;

/**
 * The editable view of an {@link ApplicationDescriptor} handed to an {@link ApplicationDescriptorEditor}. Every field starts with
 * the value of {@link #source}; a field left as is keeps its value, a field set to {@code null} is cleared.
 * The {@link #icon} left as is (the same icon as the source) keeps the persisted icon, {@code null} removes it, any other icon
 * replaces it.
 */
public final class EditableApplicationDescriptor
{
    public final @NonNull ApplicationDescriptor source;

    public String title;

    public String titleI18nKey;

    public String description;

    public String descriptionI18nKey;

    public String vendorName;

    public String vendorUrl;

    public String url;

    public GenericValue schemaConfig;

    public Icon icon;

    public EditableApplicationDescriptor( final ApplicationDescriptor source )
    {
        this.source = requireNonNull( source );
        this.title = source.getTitle();
        this.titleI18nKey = source.getTitleI18nKey();
        this.description = source.getDescription();
        this.descriptionI18nKey = source.getDescriptionI18nKey();
        this.vendorName = source.getVendorName();
        this.vendorUrl = source.getVendorUrl();
        this.url = source.getUrl();
        this.schemaConfig = source.getSchemaConfig();
        this.icon = source.getIcon();
    }

    /**
     * The edited descriptor, keyed as the source. The icon is not included: it is persisted apart from the descriptor.
     */
    public ApplicationDescriptor build()
    {
        final ApplicationDescriptor.Builder builder = ApplicationDescriptor.create()
            .key( source.getKey() )
            .title( new LocalizedText( title, titleI18nKey ) )
            .description( new LocalizedText( description, descriptionI18nKey ) )
            .vendorName( vendorName )
            .vendorUrl( vendorUrl )
            .url( url );
        if ( schemaConfig != null )
        {
            builder.schemaConfig( schemaConfig );
        }
        return builder.build();
    }
}
