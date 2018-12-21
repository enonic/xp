package com.enonic.xp.core.impl.content.page;

import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.ImageComponent;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;

public class AbstractDataSerializerTest
{
    protected PartDescriptorService partDescriptorService;

    protected LayoutDescriptorService layoutDescriptorService;

    protected ContentService contentService;

    public void setUp()
    {
        this.partDescriptorService = Mockito.mock( PartDescriptorService.class );
        this.layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );
        this.contentService = Mockito.mock( ContentService.class );
    }

    protected ImageComponent createImageComponent( final String imageId, final String imageDisplayName, final PropertyTree imageConfig )
    {
        final ContentId id = ContentId.from( imageId );
        final Content imageContent = Content.create().
            name( "someimage" ).
            displayName( imageDisplayName ).
            parentPath( ContentPath.ROOT ).
            build();

        Mockito.when( contentService.getById( id ) ).thenReturn( imageContent );

        return ImageComponent.create().
            name( imageDisplayName ).
            image( id ).
            config( imageConfig ).
            build();
    }

    protected FragmentComponent createFragmentComponent( final String fragmentId, final String fragmentDisplayName )
    {
        final ContentId id = ContentId.from( fragmentId );
        final Content fragmentContent = Content.create().
            name( "somefragment" ).
            displayName( fragmentDisplayName ).
            parentPath( ContentPath.ROOT ).
            build();

        Mockito.when( contentService.getById( id ) ).thenReturn( fragmentContent );

        return FragmentComponent.create().
            name( fragmentDisplayName ).
            fragment( id ).
            build();
    }

    protected PartComponent createPartComponent( final String partName, final String descriptorKey, final PropertyTree partConfig )
    {
        final DescriptorKey descriptor = DescriptorKey.from( descriptorKey );

        Mockito.when( partDescriptorService.getByKey( descriptor ) ).thenReturn( PartDescriptor.create().
            key( descriptor ).
            displayName( partName ).
            config( Form.create().build() ).
            build() );

        return PartComponent.create().
            name( partName ).
            descriptor( descriptor ).
            config( partConfig ).
            build();
    }
}
