package com.enonic.wem.core.content.type;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.table.DataTable;
import gherkin.formatter.model.DataTableRow;

import com.enonic.wem.core.content.type.configitem.ConfigItemPath;
import com.enonic.wem.core.content.type.configitem.ConfigItemType;
import com.enonic.wem.core.content.type.configitem.Field;
import com.enonic.wem.core.content.type.configitem.FieldTemplate;
import com.enonic.wem.core.content.type.configitem.FieldTemplateBuilder;
import com.enonic.wem.core.content.type.configitem.MockTemplateReferenceFetcher;
import com.enonic.wem.core.content.type.configitem.TemplateQualifiedName;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;
import com.enonic.wem.core.module.Module;

import static com.enonic.wem.core.content.type.configitem.TemplateReference.newTemplateReference;
import static com.enonic.wem.core.module.Module.newModule;
import static org.junit.Assert.*;

public class ContentTypeStepDefs
{
    public final MockTemplateReferenceFetcher mockTemplateReferenceFetcher = new MockTemplateReferenceFetcher();

    public final Map<String, Module> moduleByName = new HashMap<String, Module>();

    public final Map<String, ContentType> contentTypeByName = new HashMap<String, ContentType>();

    public final Map<String, Field> fieldByName = new HashMap<String, Field>();

    public final Map<TemplateQualifiedName, FieldTemplate> fieldTemplateByTemplateQualifiedName =
        new HashMap<TemplateQualifiedName, FieldTemplate>();


    @Given("^a Module named (.+)$")
    public void a_module_named_name( String name )
        throws Throwable
    {
        Module module = newModule().name( name ).build();
        moduleByName.put( name, module );
    }

    @Given("^a Field named (.+) of type (.+)$")
    public void a_field_named_name_of_type_type( String fieldName, String fieldTypeName )
        throws Throwable
    {
        Field field = Field.newBuilder().name( fieldName ).type( FieldTypes.parse( fieldTypeName ) ).build();
        fieldByName.put( fieldName, field );
    }

    @Given("^a FieldTemplate named (.+) in module (.+) with field (.+)$")
    public void a_fieldTemplate_named_name_in_module_module_having_field( String fieldTemplateName, String moduleName, String fieldName )
        throws Throwable
    {
        FieldTemplate fieldTemplate =
            FieldTemplateBuilder.newFieldTemplate().module( moduleByName.get( moduleName ) ).field( fieldByName.get( fieldName ) ).build();
        fieldTemplateByTemplateQualifiedName.put( new TemplateQualifiedName( moduleName, fieldTemplateName ), fieldTemplate );
    }

    @Given("^a ContentType named (.+)")
    public void a_ContentType_named_name( String contentTypeName )
        throws Throwable
    {
        ContentType contentType = new ContentType();
        contentType.setName( contentTypeName );
        contentTypeByName.put( contentTypeName, contentType );
    }

    @Given("^adding TemplateReference named (.+) referencing FieldTemplate (.+) to ContentType (.+)$")
    public void adding_TemplateReference_named_name_referencing_FieldTemplate_name_to_ContentType_myContentType(
        String templateReferenceName, String templateQualifiedName, String contentTypeName )
        throws Throwable
    {

        FieldTemplate fieldTemplate = fieldTemplateByTemplateQualifiedName.get( new TemplateQualifiedName( templateQualifiedName ) );
        ContentType contentType = contentTypeByName.get( contentTypeName );
        contentType.addConfigItem( newTemplateReference( fieldTemplate ).name( templateReferenceName ).build() );

        mockTemplateReferenceFetcher.add( fieldTemplate );
        contentType.templateReferencesToConfigItems( mockTemplateReferenceFetcher );
    }

    @When("^translating template references to configItems for all content types$")
    public void translating_template_references_to_configItems_for_all_content_types()
        throws Throwable
    {

        for ( ContentType contentType : contentTypeByName.values() )
        {
            contentType.templateReferencesToConfigItems( mockTemplateReferenceFetcher );
        }
    }

    @Then("^the following ConfigItems should exist in the following ContentTypes:$")
    public void the_following_ConfigItems_should_exist_in_the_following_ContentTypes( DataTable dataTable )
        throws Throwable
    {

        List<DataTableRow> list = dataTable.getGherkinRows();
        for ( DataTableRow row : list )
        {
            String contentTypeName = row.getCells().get( 0 );
            String configItemPath = row.getCells().get( 1 );
            ConfigItemType configItemType = ConfigItemType.valueOf( row.getCells().get( 2 ) );
            ContentType contentType = contentTypeByName.get( contentTypeName );
            assertNotNull( "configItem not found at path: " + configItemPath,
                           contentType.getConfigItems().getConfigItem( new ConfigItemPath( configItemPath ) ) );
            assertEquals( ConfigItemType.FIELD, configItemType );
        }
    }

}
