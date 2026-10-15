package com.enonic.xp.lib.schema;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.CreateOrUpdateCmsParams;
import com.enonic.xp.resource.CreateOrUpdateComponentParams;
import com.enonic.xp.resource.CreateOrUpdateContentSchemaParams;
import com.enonic.xp.resource.CreateOrUpdateStylesParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.style.StyleDescriptor;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The generic {@code createSchema}, {@code getComponent}, {@code getSite} etc. are deprecated wrappers that dispatch on
 * {@code type} to the per-type functions. The service is mocked to echo the requested name back.
 */
class DeprecatedFunctionsTest
    extends BaseSchemaHandlerTest
{
    @BeforeEach
    void setUpMocks()
    {
        when( dynamicSchemaService.createOrUpdateContentType( isA( CreateOrUpdateContentSchemaParams.class ) ) ).thenAnswer(
            invocation -> contentTypeResult(
                (ContentTypeName) invocation.getArgument( 0, CreateOrUpdateContentSchemaParams.class ).getName() ) );
        when( dynamicSchemaService.createOrUpdateFormFragment( isA( CreateOrUpdateContentSchemaParams.class ) ) ).thenAnswer(
            invocation -> formFragmentResult(
                (FormFragmentName) invocation.getArgument( 0, CreateOrUpdateContentSchemaParams.class ).getName() ) );
        when( dynamicSchemaService.getMixin( isA( MixinName.class ) ) ).thenAnswer(
            invocation -> mixinResult( invocation.getArgument( 0, MixinName.class ) ) );
        when( dynamicSchemaService.listContentTypes( isA( ApplicationKey.class ) ) ).thenAnswer(
            invocation -> List.of( contentTypeResult( ContentTypeName.from( "myapp:mytype" ) ) ) );
        when( dynamicSchemaService.deleteContentType( isA( ContentTypeName.class ) ) ).thenReturn( true );

        when( dynamicSchemaService.createOrUpdatePart( isA( CreateOrUpdateComponentParams.class ) ) ).thenAnswer(
            invocation -> partResult( invocation.getArgument( 0, CreateOrUpdateComponentParams.class ).getKey() ) );
        when( dynamicSchemaService.getPart( isA( DescriptorKey.class ) ) ).thenAnswer(
            invocation -> partResult( invocation.getArgument( 0, DescriptorKey.class ) ) );
        when( dynamicSchemaService.listParts( isA( ApplicationKey.class ) ) ).thenAnswer(
            invocation -> List.of( partResult( DescriptorKey.from( "myapp:mypart" ) ) ) );
        when( dynamicSchemaService.deletePart( isA( DescriptorKey.class ) ) ).thenReturn( true );

        when( dynamicSchemaService.getCmsDescriptor( isA( ApplicationKey.class ) ) ).thenAnswer(
            invocation -> cmsResult( invocation.getArgument( 0, ApplicationKey.class ) ) );
        when( dynamicSchemaService.createOrUpdateCms( isA( CreateOrUpdateCmsParams.class ) ) ).thenAnswer(
            invocation -> cmsResult( invocation.getArgument( 0, CreateOrUpdateCmsParams.class ).getKey() ) );
        when( dynamicSchemaService.createOrUpdateStyles( isA( CreateOrUpdateStylesParams.class ) ) ).thenAnswer(
            invocation -> stylesResult( invocation.getArgument( 0, CreateOrUpdateStylesParams.class ).getKey() ) );
    }

    @Test
    void createSchema()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "createSchema" );
    }

    @Test
    void updateSchema()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "updateSchema" );
    }

    @Test
    void getSchema()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "getSchema" );
    }

    @Test
    void listSchemas()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "listSchemas" );
    }

    @Test
    void deleteSchema()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "deleteSchema" );
    }

    @Test
    void unsupportedSchemaType()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "unsupportedSchemaType" );
    }

    @Test
    void createComponent()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "createComponent" );
    }

    @Test
    void updateComponent()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "updateComponent" );
    }

    @Test
    void getComponent()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "getComponent" );
    }

    @Test
    void listComponents()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "listComponents" );
    }

    @Test
    void deleteComponent()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "deleteComponent" );
    }

    @Test
    void unsupportedComponentType()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "unsupportedComponentType" );
    }

    @Test
    void getSite()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "getSite" );
    }

    @Test
    void updateSite()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "updateSite" );
    }

    @Test
    void updateCms()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "updateCms" );
    }

    @Test
    void createStyles()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "createStyles" );
    }

    @Test
    void updateStyles()
    {
        runFunction( "/test/DeprecatedFunctionsTest.js", "updateStyles" );
    }

    private static DynamicSchemaResult<StyleDescriptor> stylesResult( final ApplicationKey key )
    {
        return new DynamicSchemaResult<>( StyleDescriptor.create().application( key ).build(), resource() );
    }

    private static DynamicSchemaResult<ContentType> contentTypeResult( final ContentTypeName name )
    {
        return new DynamicSchemaResult<>( ContentType.create().name( name ).superType( ContentTypeName.structured() ).build(),
                                          resource() );
    }

    private static DynamicSchemaResult<FormFragmentDescriptor> formFragmentResult( final FormFragmentName name )
    {
        return new DynamicSchemaResult<>( FormFragmentDescriptor.create().name( name ).build(), resource() );
    }

    private static DynamicSchemaResult<MixinDescriptor> mixinResult( final MixinName name )
    {
        return new DynamicSchemaResult<>( MixinDescriptor.create().name( name ).build(), resource() );
    }

    private static DynamicSchemaResult<PartDescriptor> partResult( final DescriptorKey key )
    {
        return new DynamicSchemaResult<>( PartDescriptor.create().key( key ).config( Form.empty() ).build(), resource() );
    }

    private static DynamicSchemaResult<CmsDescriptor> cmsResult( final ApplicationKey key )
    {
        return new DynamicSchemaResult<>( CmsDescriptor.create().applicationKey( key ).build(), resource() );
    }

    private static Resource resource()
    {
        final Resource resource = mock( Resource.class );
        when( resource.readString() ).thenReturn( "" );
        return resource;
    }
}
