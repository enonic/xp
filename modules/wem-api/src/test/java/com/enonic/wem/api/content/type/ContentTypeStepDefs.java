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

import com.enonic.wem.api.content.type.formitem.ComponentSubType;
import com.enonic.wem.api.content.type.formitem.ComponentSubTypeBuilder;
import com.enonic.wem.api.content.type.formitem.FormItemPath;
import com.enonic.wem.api.content.type.formitem.FormItemType;
import com.enonic.wem.api.content.type.formitem.Input;
import com.enonic.wem.api.content.type.formitem.MockSubTypeFetcher;
import com.enonic.wem.api.content.type.formitem.SubTypeQualifiedName;
import com.enonic.wem.api.content.type.formitem.SubTypeReference;
import com.enonic.wem.api.content.type.formitem.comptype.ComponentTypes;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.formitem.Input.newInput;

public class ContentTypeStepDefs
{
    public final MockSubTypeFetcher mockSubTypeFetcher = new MockSubTypeFetcher();

    public final Map<String, Module> moduleByName = new HashMap<String, Module>();

    public final Map<String, ContentType> contentTypeByName = new HashMap<String, ContentType>();

    public final Map<String, Input> inputByName = new HashMap<String, Input>();

    public final Map<SubTypeQualifiedName, ComponentSubType> componentSubTypeByQualifiedName =
        new HashMap<SubTypeQualifiedName, ComponentSubType>();


    @Given("^a Module named (.+)$")
    public void a_module_named_name( String name )
        throws Throwable
    {
        Module module = Module.newModule().name( name ).build();
        moduleByName.put( name, module );
    }

    @Given("^a Input named (.+) of type (.+)$")
    public void a_input_named_name_of_type_type( String componentName, String componentTypeName )
        throws Throwable
    {
        Input input = newInput().name( componentName ).type( ComponentTypes.parse( componentTypeName ) ).build();
        inputByName.put( componentName, input );
    }

    @Given("^a ComponentSubType named (.+) in module (.+) with input (.+)$")
    public void a_componentSubType_named_name_in_module_module_having_input( String subTypeName, String moduleName, String inputName )
        throws Throwable
    {
        ComponentSubType componentSubType = ComponentSubTypeBuilder.newComponentSubType().module( moduleByName.get( moduleName ) ).input(
            inputByName.get( inputName ) ).build();
        componentSubTypeByQualifiedName.put( new SubTypeQualifiedName( moduleName, subTypeName ), componentSubType );
    }

    @Given("^a ContentType named (.+)")
    public void a_ContentType_named_name( String contentTypeName )
        throws Throwable
    {
        ContentType contentType = new ContentType();
        contentType.setName( contentTypeName );
        contentTypeByName.put( contentTypeName, contentType );
    }

    @Given("^adding SubTypeReference named (.+) referencing ComponentSubType (.+) to ContentType (.+)$")
    public void adding_SubTypeReference_named_name_referencing_ComponentSubType_name_to_ContentType_myContentType(
        String subTypeReferenceName, String subTypeQualifiedName, String contentTypeName )
        throws Throwable
    {

        ComponentSubType componentSubType = componentSubTypeByQualifiedName.get( new SubTypeQualifiedName( subTypeQualifiedName ) );
        ContentType contentType = contentTypeByName.get( contentTypeName );
        contentType.addFormItem( SubTypeReference.newSubTypeReference( componentSubType ).name( subTypeReferenceName ).build() );

        mockSubTypeFetcher.add( componentSubType );
        contentType.subTypeReferencesToFormItems( mockSubTypeFetcher );
    }

    @When("^translating subType references to formItems for all content types$")
    public void translating_subType_references_to_formItems_for_all_content_types()
        throws Throwable
    {
        for ( ContentType contentType : contentTypeByName.values() )
        {
            contentType.subTypeReferencesToFormItems( mockSubTypeFetcher );
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
            Assert.assertEquals( FormItemType.INPUT, formItemType );
        }
    }

}
