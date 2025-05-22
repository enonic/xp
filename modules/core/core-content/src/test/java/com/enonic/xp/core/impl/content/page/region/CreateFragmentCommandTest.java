package com.enonic.xp.core.impl.content.page.region;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.CreateFragmentParams;
import com.enonic.xp.region.ImageComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.region.TextComponent;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CreateFragmentCommandTest
{
    private ContentService contentService;

    private PartDescriptorService partDescriptorService;

    private LayoutDescriptorService layoutDescriptorService;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
        this.partDescriptorService = Mockito.mock( PartDescriptorService.class );
        this.layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );
    }

    @Test
    public void partComponentName_emptyDisplayName()
    {
        assertEquals( "Part", testPartComponentName( "" ) );
    }

    @Test
    public void partComponentName_nullDisplayName()
    {
        assertEquals( "Part", testPartComponentName( null ) );
    }

    @Test
    public void partComponentName()
    {
        assertEquals( "Part Descriptor Name", testPartComponentName( "Part Descriptor Name" ) );
    }

    @Test
    public void layoutComponentName_emptyDisplayName()
    {
        assertEquals( "Layout", testLayoutComponentName( "" ) );

    }

    @Test
    public void layoutComponentName_nullDisplayName()
    {
        assertEquals( "Layout", testLayoutComponentName( null ) );
    }

    @Test
    public void layoutComponentName()
    {
        assertEquals( "Layout Descriptor Name", testLayoutComponentName( "Layout Descriptor Name" ) );
    }


    @Test
    public void textComponentName_nullValue()
    {
        assertEquals( "Text", testTextComponentName( null ) );
    }

    @Test
    public void textComponentName_emptyValue()
    {
        assertEquals( "Text", testTextComponentName( "" ) );

    }

    @Test
    public void textComponentName()
    {
        assertEquals( "My Text Component", testTextComponentName( "My Text Component" ) );
    }

    @Test
    public void imageComponentName_contentNotFound()
    {
        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).thenThrow( ContentNotFoundException.class );

        assertEquals( "Image", testImageComponentName( null, false ) );
    }

    @Test
    public void imageComponentName_nullDisplayName()
    {
        assertEquals( "Image", testImageComponentName( null, true ) );
    }

    @Test
    public void imageComponentName_emptyDisplayName()
    {
        assertEquals( "", testImageComponentName( "", true ) );
    }

    @Test
    public void imageComponentName()
    {
        assertEquals( "Image Display Name", testImageComponentName( "Image Display Name", true ) );
    }

    private PartDescriptor partDescriptor( final DescriptorKey descriptorKey, final String displayName )
    {
        return PartDescriptor.create().
            key( descriptorKey ).
            displayName( displayName ).
            config( Form.create().build() ).
            build();
    }

    private LayoutDescriptor layoutDescriptor( final DescriptorKey descriptorKey, final String displayName )
    {
        return LayoutDescriptor.create().
            key( descriptorKey ).
            displayName( displayName ).
            config( Form.create().build() ).
            regions( RegionDescriptors.create().build() ).
            build();
    }

    private ImageComponent imageComponent( final ContentId contentId )
    {
        return ImageComponent.create().
            image( contentId ).
            config( new PropertyTree() ).
            build();
    }

    private PartComponent partComponent( final DescriptorKey descriptorKey )
    {
        return PartComponent.create().
            descriptor( descriptorKey ).
            config( new PropertyTree() ).
            build();
    }

    private LayoutComponent layoutComponent( final DescriptorKey descriptorKey )
    {
        return LayoutComponent.create().
            descriptor( descriptorKey ).
            config( new PropertyTree() ).
            build();
    }

    private TextComponent textComponent( final String text )
    {
        return TextComponent.create().
            text( text ).
            build();
    }

    private Content content( final String displayName )
    {
        return Content.create().
            id( ContentId.from( "content-id" ) ).
            path( "/path/to/content" ).
            displayName( displayName ).
            type( ContentTypeName.folder() ).
            name( "content" ).
            build();
    }

    private CreateFragmentParams createFragmentParams( final Component component )
    {
        final PropertyTree configData = new PropertyTree();
        configData.addString( "myData", "aaa" );

        return CreateFragmentParams.create().
            config( configData ).
            parent( ContentPath.ROOT ).
            component( component ).
            workflowInfo( WorkflowInfo.create().
                state( WorkflowState.READY ).
                build() ).
            build();
    }

    private CreateFragmentCommand createFragmentCommand( CreateFragmentParams params )
    {
        return CreateFragmentCommand.create().
            params( params ).
            contentService( this.contentService ).
            partDescriptorService( this.partDescriptorService ).
            layoutDescriptorService( this.layoutDescriptorService ).
            build();
    }

    private String testPartComponentName( final String name )
    {
        final ArgumentCaptor<CreateContentParams> captor = captorFragmentCreation();

        final DescriptorKey partKey = DescriptorKey.from( ApplicationKey.from( "application" ), "part1" );
        Mockito.when( this.partDescriptorService.getByKey( partKey ) ).thenReturn( partDescriptor( partKey, name ) );

        createFragmentCommand( createFragmentParams( partComponent( partKey ) ) ).
            execute();

        return captor.getValue().getDisplayName();

    }

    private String testLayoutComponentName( final String name )
    {
        final ArgumentCaptor<CreateContentParams> captor = captorFragmentCreation();

        final DescriptorKey layoutKey = DescriptorKey.from( ApplicationKey.from( "application" ), "layout1" );
        Mockito.when( this.layoutDescriptorService.getByKey( layoutKey ) ).thenReturn( layoutDescriptor( layoutKey, name ) );

        createFragmentCommand( createFragmentParams( layoutComponent( layoutKey ) ) ).
            execute();

        return captor.getValue().getDisplayName();
    }

    private String testTextComponentName( final String value )
    {
        final ArgumentCaptor<CreateContentParams> captor = captorFragmentCreation();

        createFragmentCommand( createFragmentParams( textComponent( value ) ) ).
            execute();

        return captor.getValue().getDisplayName();
    }

    private String testImageComponentName( final String name, final boolean mockImageContent )
    {
        final ArgumentCaptor<CreateContentParams> captor = captorFragmentCreation();

        final ContentId imageId = ContentId.from( "image-id" );
        if ( mockImageContent )
        {
            Mockito.when( contentService.getById( imageId ) ).thenReturn( content( name ) );
        }

        final CreateFragmentCommand command = createFragmentCommand( createFragmentParams( imageComponent( imageId ) ) );

        command.execute();

        return captor.getValue().getDisplayName();
    }

    private ArgumentCaptor<CreateContentParams> captorFragmentCreation()
    {
        final Content fragment = Mockito.mock( Content.class );
        Mockito.when( fragment.getId() ).thenReturn( ContentId.from( "fragment-id" ) );

        final ArgumentCaptor<CreateContentParams> argument = ArgumentCaptor.forClass( CreateContentParams.class );

        Mockito.when( contentService.create( argument.capture() ) ).thenReturn( fragment );

        return argument;
    }
}
