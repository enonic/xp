package com.enonic.wem.api.content.type;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.table.DataTable;
import gherkin.formatter.model.DataTableRow;

import com.enonic.wem.api.content.type.formitem.Component;
import com.enonic.wem.api.content.type.formitem.ComponentTemplate;
import com.enonic.wem.api.content.type.formitem.ComponentTemplateBuilder;
import com.enonic.wem.api.content.type.formitem.FormItemPath;
import com.enonic.wem.api.content.type.formitem.FormItemType;
import com.enonic.wem.api.content.type.formitem.MockTemplateFetcher;
import com.enonic.wem.api.content.type.formitem.TemplateQualifiedName;
import com.enonic.wem.api.content.type.formitem.TemplateReference;
import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypes;
import com.enonic.wem.api.module.Module;

public class ContentTypeStepDefs
{
    public final MockTemplateFetcher mockTemplateReferenceFetcher = new MockTemplateFetcher();

    public final Map<String, Module> moduleByName = new HashMap<String, Module>();

    public final Map<String, ContentType> contentTypeByName = new HashMap<String, ContentType>();

    public final Map<String, Component> fieldByName = new HashMap<String, Component>();

    public final Map<TemplateQualifiedName, ComponentTemplate> fieldTemplateByTemplateQualifiedName =
        new HashMap<TemplateQualifiedName, ComponentTemplate>();


    @Given("^a Module named (.+)$")
    public void a_module_named_name( String name )
        throws Throwable
    {
        Module module = Module.newModule().name( name ).build();
        moduleByName.put( name, module );
    }

    @Given("^a Field named (.+) of type (.+)$")
    public void a_field_named_name_of_type_type( String fieldName, String componentTypeName )
        throws Throwable
    {
        Component component = Component.newBuilder().name( fieldName ).type( ComponentTypes.parse( componentTypeName ) ).build();
        fieldByName.put( fieldName, component );
    }

    @Given("^a FieldTemplate named (.+) in module (.+) with field (.+)$")
    public void a_fieldTemplate_named_name_in_module_module_having_field( String fieldTemplateName, String moduleName, String fieldName )
        throws Throwable
    {
        ComponentTemplate componentTemplate =
            ComponentTemplateBuilder.newComponentTemplate().module( moduleByName.get( moduleName ) ).component(
                fieldByName.get( fieldName ) ).build();
        fieldTemplateByTemplateQualifiedName.put( new TemplateQualifiedName( moduleName, fieldTemplateName ), componentTemplate );
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

        ComponentTemplate componentTemplate =
            fieldTemplateByTemplateQualifiedName.get( new TemplateQualifiedName( templateQualifiedName ) );
        ContentType contentType = contentTypeByName.get( contentTypeName );
        contentType.addFormItem( TemplateReference.newTemplateReference( componentTemplate ).name( templateReferenceName ).build() );

        mockTemplateReferenceFetcher.add( componentTemplate );
        contentType.templateReferencesToFormItems( mockTemplateReferenceFetcher );
    }

    @When("^translating template references to formItems for all content types$")
    public void translating_template_references_to_formItems_for_all_content_types()
        throws Throwable
    {

        for ( ContentType contentType : contentTypeByName.values() )
        {
            contentType.templateReferencesToFormItems( mockTemplateReferenceFetcher );
        }
    }

    @Then("^the following FormItems should exist in the following ContentTypes:$")
    public void the_following_FormItems_should_exist_in_the_following_ContentTypes( DataTable dataTable )
        throws Throwable
    {

        List<DataTableRow> list = dataTable.getGherkinRows();
        for ( DataTableRow row : list )
        {
            String contentTypeName = row.getCells().get( 0 );
            String formItemPath = row.getCells().get( 1 );
            FormItemType formItemType = FormItemType.valueOf( row.getCells().get( 2 ) );
            ContentType contentType = contentTypeByName.get( contentTypeName );
            Assert.assertNotNull( "formItem not found at path: " + formItemPath,
                                  contentType.getFormItems().getHierarchicalFormItem( new FormItemPath( formItemPath ) ) );
            Assert.assertEquals( FormItemType.COMPONENT, formItemType );
        }
    }

}
