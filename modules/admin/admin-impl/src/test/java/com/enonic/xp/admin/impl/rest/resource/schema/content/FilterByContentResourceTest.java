package com.enonic.xp.admin.impl.rest.resource.schema.content;

import java.time.Instant;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;

import com.enonic.xp.admin.impl.json.content.JsonObjectsFactory;
import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.mixin.MixinService;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilterByContentResourceTest
    extends AdminResourceTestSupport
{
    private FilterByContentResolver filterByContentResolver;

    @Override
    protected Object getResourceInstance()
    {
        LocaleService localeService = mock( LocaleService.class );
        MixinService mixinService = mock( MixinService.class );
        when( mixinService.inlineFormItems( isA( Form.class ) ) ).then( AdditionalAnswers.returnsFirstArg() );

        ContentTypeService contentTypeService = mock( ContentTypeService.class );
        filterByContentResolver = mock( FilterByContentResolver.class );
        final JsonObjectsFactory jsonObjectsFactory = new JsonObjectsFactory();
        jsonObjectsFactory.setLocaleService( localeService );
        jsonObjectsFactory.setMixinService( mixinService );
        jsonObjectsFactory.setContentTypeService( contentTypeService );
        final FilterByContentResource resource = new FilterByContentResource();
        resource.setJsonObjectsFactory( jsonObjectsFactory );
        resource.setFilterByContentResolver( filterByContentResolver );
        return resource;
    }

    @Test
    void contentTypes()
        throws Exception
    {
        final ContentType contentTypeToMap = ContentType.create()
            .superType( ContentTypeName.structured() )
            .displayName( "My type" )
            .name( "application:test-type" )
            .icon( Icon.from( new byte[]{123}, "image/gif", Instant.now() ) )
            .build();

        when( filterByContentResolver.contentTypes( ContentId.from( "test" ) ) ).thenReturn( Stream.of( contentTypeToMap ) );
        String jsonString =
            request().path( "cms/default/base/schema/filter/contentTypes" ).queryParam( "contentId", "test" ).get().getAsString();

        assertJson( "filter_content_types.json", jsonString );
    }

    @Test
    void contentTypes_root()
        throws Exception
    {
        final ContentType contentTypeToMap = ContentType.create()
            .superType( ContentTypeName.structured() )
            .displayName( "My type" )
            .name( "application:test-type" )
            .icon( Icon.from( new byte[]{123}, "image/gif", Instant.now() ) )
            .build();

        when( filterByContentResolver.contentTypes( isNull() ) ).thenReturn( Stream.of( contentTypeToMap ) );
        String jsonString = request().path( "cms/default/base/schema/filter/contentTypes" ).get().getAsString();

        assertJson( "filter_content_types.json", jsonString );
    }

    @Test
    void layouts()
        throws Exception
    {
        final Form layoutForm = Form.create()
            .addFormItem( Input.create().name( "columns" ).label( "columns" ).inputType( InputTypeName.DOUBLE ).build() )
            .build();

        final LayoutDescriptor layoutDescriptor = LayoutDescriptor.create()
            .displayName( "Fancy layout" )
            .config( layoutForm )
            .regions( RegionDescriptors.create().build() )
            .key( DescriptorKey.from( "module:fancy-layout" ) )
            .build();

        when( filterByContentResolver.layouts( ContentId.from( "test" ) ) ).thenReturn( Stream.of( layoutDescriptor ) );
        String jsonString =
            request().path( "cms/default/base/schema/filter/layouts" ).queryParam( "contentId", "test" ).get().getAsString();

        assertJson( "filter_layouts.json", jsonString );
    }

    @Test
    void parts()
        throws Exception
    {
        final Form form = Form.create()
            .addFormItem( Input.create().name( "columns" ).label( "columns" ).inputType( InputTypeName.DOUBLE ).build() )
            .build();

        final PartDescriptor layoutDescriptor =
            PartDescriptor.create().displayName( "Fancy part" ).config( form ).key( DescriptorKey.from( "module:fancy-part" ) ).build();

        when( filterByContentResolver.parts( ContentId.from( "test" ) ) ).thenReturn( Stream.of( layoutDescriptor ) );
        String jsonString = request().path( "cms/default/base/schema/filter/parts" ).queryParam( "contentId", "test" ).get().getAsString();

        assertJson( "filter_parts.json", jsonString );
    }

    @Test
    void pages()
        throws Exception
    {
        final Form form = Form.create()
            .addFormItem( Input.create().name( "columns" ).label( "columns" ).inputType( InputTypeName.DOUBLE ).build() )
            .build();

        final PageDescriptor layoutDescriptor = PageDescriptor.create()
            .displayName( "Fancy page" )
            .config( form )
            .regions( RegionDescriptors.create().build() )
            .key( DescriptorKey.from( "module:fancy-page" ) )
            .build();

        when( filterByContentResolver.pages( ContentId.from( "test" ) ) ).thenReturn( Stream.of( layoutDescriptor ) );
        String jsonString = request().path( "cms/default/base/schema/filter/pages" ).queryParam( "contentId", "test" ).get().getAsString();

        assertJson( "filter_pages.json", jsonString );
    }
}
