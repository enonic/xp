package com.enonic.xp.lib.schema;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.XDataMapping;
import com.enonic.xp.site.XDataMappings;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetDynamicSiteHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    public void testSite()
    {
        when( dynamicSchemaService.getCmsDescriptor( isA( ApplicationKey.class ) ) ).thenAnswer( params -> {
            final ApplicationKey applicationKey = params.getArgument( 0, ApplicationKey.class );

            final FormItem formItem = Input.create().name( "input" ).label( "Input" ).inputType( InputTypeName.DOUBLE ).build();

            final Form form = Form.create().addFormItem( formItem ).build();

            List<XDataMapping> xDataMappingList = new ArrayList<>();
            xDataMappingList.add( XDataMapping.create().xDataName( XDataName.from( "myapplication:my" ) ).build() );
            XDataMappings xDataMappings = XDataMappings.from( xDataMappingList );

            CmsDescriptor cmsDescriptor = CmsDescriptor.create()
                .applicationKey( applicationKey )
                .modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) )
                .form( form )
                .xDataMappings( xDataMappings )
                .build();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( "<site><some-data></some-data></site>" );

            return new DynamicSchemaResult<>( cmsDescriptor, resource );
        } );

        runScript( "/lib/xp/examples/schema/getSite.js" );
    }


    @Test
    public void testNull()
    {
        runFunction( "/test/GetDynamicSiteHandlerTest.js", "getNull" );
    }

}
