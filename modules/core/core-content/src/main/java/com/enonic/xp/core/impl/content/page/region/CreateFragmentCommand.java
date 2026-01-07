package com.enonic.xp.core.impl.content.page.region;

import java.util.Objects;
import java.util.stream.IntStream;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.core.internal.HtmlHelper;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.page.EditablePage;
import com.enonic.xp.page.Page;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.region.CreateFragmentParams;
import com.enonic.xp.region.ImageComponent;
import com.enonic.xp.region.ImageComponentType;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutComponentType;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartComponentType;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.TextComponent;
import com.enonic.xp.region.TextComponentType;
import com.enonic.xp.schema.content.ContentTypeName;

final class CreateFragmentCommand
{
    private final CreateFragmentParams params;

    private final ContentService contentService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private CreateFragmentCommand( Builder builder )
    {
        params = builder.params;
        contentService = builder.contentService;
        partDescriptorService = builder.partDescriptorService;
        layoutDescriptorService = builder.layoutDescriptorService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Content execute()
    {
        final String displayName = generateDisplayName( params.getComponent() );
        final String name = generateUniqueContentName( params.getParent(), "fragment-" + displayName );

        final CreateContentParams createContent = CreateContentParams.create().
            parent( params.getParent() ).
            displayName( displayName ).
            name( name ).
            type( ContentTypeName.fragment() ).
            contentData( new PropertyTree() ).
            workflowInfo( params.getWorkflowInfo() ).
            page( Page.create().
            config( this.params.getConfig() ).
            fragment( this.params.getComponent() ).
            build() ).
            build();

        return contentService.create( createContent );
    }

    private String generateDisplayName( final Component component )
    {
        if ( component.getType() instanceof TextComponentType )
        {
            return doGenerateDisplayName( (TextComponent) component );
        }

        if ( component.getType() instanceof ImageComponentType )
        {
            return doGenerateDisplayName( (ImageComponent) component );
        }

        if ( component.getType() instanceof PartComponentType )
        {
            return doGenerateDisplayName( (PartComponent) component );
        }

        if ( component.getType() instanceof LayoutComponentType )
        {
            return doGenerateDisplayName( (LayoutComponent) component );
        }

        return component.getType().toString();
    }

    private String doGenerateDisplayName( final TextComponent textComponent )
    {
        final String html = textComponent.getText();
        String text = HtmlHelper.htmlToText( html );
        return text.isEmpty() ? "Text" : abbreviate( text, 40 );
    }

    private String doGenerateDisplayName( final ImageComponent imageComponent )
    {
        if ( imageComponent.getImage() != null )
        {
            try
            {
                final Content image = this.contentService.getById( imageComponent.getImage() );

                if ( image.getDisplayName() != null )
                {
                    return image.getDisplayName();
                }
            }
            catch ( ContentNotFoundException e )
            {
            }
        }

        return "Image";
    }

    private String doGenerateDisplayName( final PartComponent partComponent )
    {
        return doGenerateDisplayName(
            partComponent.hasDescriptor() ? this.partDescriptorService.getByKey( partComponent.getDescriptor() ) : null, "Part" );
    }

    private String doGenerateDisplayName( final LayoutComponent layoutComponent )
    {
        return doGenerateDisplayName(
            layoutComponent.hasDescriptor() ? this.layoutDescriptorService.getByKey( layoutComponent.getDescriptor() ) : null, "Layout" );
    }

    private String doGenerateDisplayName( final ComponentDescriptor componentDescriptor, final String defaultName )
    {
        if ( componentDescriptor != null && componentDescriptor.getDisplayName() != null )
        {
            return componentDescriptor.getDisplayName();
        }
        return defaultName;
    }

    private String generateUniqueContentName( final ContentPath parent, final String displayName )
    {
        final String baseName = NamePrettyfier.create( displayName );

        String name = baseName;
        int counter = 1;
        while ( this.contentService.contentExists( ContentPath.from( parent, name ) ) )
        {
            final String suffix = Integer.toString( ++counter );
            name = NamePrettyfier.create( baseName + "-" + suffix );
        }

        return name;
    }

    /**
     * Abbreviates a String using ellipsis.
     *
     * @param string    string to abbreviate
     * @param maxLength maximum code points
     * @return abbreviated string
     */
    private static String abbreviate( String string, int maxLength )
    {
        final String ellipsis = "...";
        final boolean useEllipsis = string.codePointCount( 0, string.length() ) > Math.max( maxLength, ellipsis.length() );

        return IntStream.concat( string.codePoints().limit( useEllipsis ? maxLength - ellipsis.length() : maxLength ),
                                 useEllipsis ? ellipsis.codePoints() : IntStream.empty() ).
            collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append ).toString();
    }

    public static final class Builder
    {
        private CreateFragmentParams params;

        private ContentService contentService;

        private PartDescriptorService partDescriptorService;

        private LayoutDescriptorService layoutDescriptorService;

        private Builder()
        {
        }

        public Builder params( CreateFragmentParams params )
        {
            this.params = params;
            return this;
        }

        public Builder contentService( ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public Builder layoutDescriptorService( LayoutDescriptorService layoutDescriptorService )
        {
            this.layoutDescriptorService = layoutDescriptorService;
            return this;
        }

        public Builder partDescriptorService( PartDescriptorService partDescriptorService )
        {
            this.partDescriptorService = partDescriptorService;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( contentService );
            Objects.requireNonNull( partDescriptorService );
            Objects.requireNonNull( layoutDescriptorService );
            Objects.requireNonNull( params, "params cannot be null" );
        }

        public CreateFragmentCommand build()
        {
            validate();
            return new CreateFragmentCommand( this );
        }
    }
}
