package com.enonic.xp.core.impl.content.index;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.core.impl.content.index.ContentIndexConfigFactory;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.Input;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;

import static org.junit.Assert.*;

public class ContentIndexConfigFactoryTest
{
    protected ContentTypeService contentTypeService;

    protected PageDescriptorService pageDescriptorService;

    @Before
    public void setUp()
        throws Exception
    {
        contentTypeService = Mockito.mock( ContentTypeService.class );
        pageDescriptorService = Mockito.mock( PageDescriptorService.class );
    }

    @Test
    public void media_indexing()
        throws Exception
    {
/*        final PropertyTree data = new PropertyTree();
        data.setString( "myString", "myStringValue" );
        final PropertySet metadata = data.addSet( ContentPropertyNames.METADATA );
        metadata.addString( "media", "imageMedia" );
        metadata.addDouble( "double", 13d );
        metadata.addString( "no-index", "no-index-value" );
        final PropertySet subSet = metadata.addSet( "subSet" );
        subSet.addString( "subSetValue", "promp" );*/

        final Form form = Form.create().build();
        final ContentType contentType =
            ContentType.create().superType( ContentTypeName.structured() ).name( "myapplication:test" ).form( form ).build();
        Mockito.when( contentTypeService.getByName( Mockito.any() ) ).thenReturn( contentType );

        final IndexConfigDocument indexConfigDocument = ContentIndexConfigFactory.create().
            contentTypeName( ContentTypeName.imageMedia() ).
            contentTypeService( contentTypeService ).
            build().produce();

        assertEquals( IndexConfig.MINIMAL, indexConfigDocument.getConfigForPath( PropertyPath.from( ContentPropertyNames.EXTRA_DATA ) ) );

        assertEquals( IndexConfig.MINIMAL,
                      indexConfigDocument.getConfigForPath( PropertyPath.from( ContentPropertyNames.EXTRA_DATA, "media" ) ) );

        assertEquals( IndexConfig.MINIMAL,
                      indexConfigDocument.getConfigForPath( PropertyPath.from( ContentPropertyNames.EXTRA_DATA, "subSet" ) ) );

        assertEquals( IndexConfig.MINIMAL, indexConfigDocument.getConfigForPath(
            PropertyPath.from( ContentPropertyNames.EXTRA_DATA, "subSet", "subSetValue" ) ) );
    }
}