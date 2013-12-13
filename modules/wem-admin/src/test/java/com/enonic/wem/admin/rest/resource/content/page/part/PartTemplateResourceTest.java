package com.enonic.wem.admin.rest.resource.content.page.part;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.part.GetPartTemplatesBySiteTemplate;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateKey;
import com.enonic.wem.api.content.page.part.PartTemplateName;
import com.enonic.wem.api.content.page.part.PartTemplates;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;

public class PartTemplateResourceTest
    extends AbstractResourceTest
{
    private Client client;

    @Before
    public void setup()
    {
        mockCurrentContextHttpRequest();
    }

    @Override
    protected Object getResourceInstance()
    {
        client = Mockito.mock( Client.class );
        final PartTemplateResource resource = new PartTemplateResource();

        resource.setClient( client );
        return resource;
    }

    @Test
    public void test_list_of_part_templates()
        throws Exception
    {

        Mockito.when( client.execute( Mockito.isA( GetPartTemplatesBySiteTemplate.class ) ) ).thenReturn( createTemplates() );
        String result = resource().path( "content/page/part/template/list" ).queryParam( "key", "sitetemplate-1.0.0" ).get( String.class );
        assertJson( "part_template_list.json", result );
    }

    @Test
    public void test_empty_list_of_part_templates()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetPartTemplatesBySiteTemplate.class ) ) ).thenReturn( PartTemplates.empty() );
        String result = resource().path( "content/page/part/template/list" ).queryParam( "key", "sitetemplate-1.0.0" ).get( String.class );
        assertJson( "part_template_empty_list.json", result );
    }

    @Test(expected = SiteTemplateNotFoundException.class)
    public void test_list_of_part_templates_of_fake_site_template()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( GetPartTemplatesBySiteTemplate.class ) ) ).thenThrow(
            new SiteTemplateNotFoundException( SiteTemplateKey.from( "sitetemplate-1.0.0" ) ) );
        String result = resource().path( "content/page/part/template/list" ).queryParam( "key", "sitetemplate-1.0.0" ).get( String.class );
        assertJson( "part_template_list_with_exception.json", result );
    }

    private PartTemplates createTemplates()
    {
        return PartTemplates.from( createTemplate( "partTemplate1" ), createTemplate( "partTemplate2" ) );
    }

    private PartTemplate createTemplate( String key )
    {
        return PartTemplate.newPartTemplate().key( PartTemplateKey.from( "sitetemplate-1.0.0|module-1.0.0|" + key ) ).displayName(
            key ).name( new PartTemplateName( key ) ).parentPath( ResourcePath.root() ).descriptor(
            ModuleResourceKey.from( "module-1.0.0:/tpl.xml" ) ).build();
    }
}
