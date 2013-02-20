package com.enonic.wem.api.content.schema.type;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.table.DataTable;
import gherkin.formatter.model.DataTableRow;

import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.content.schema.mixin.MockMixinFetcher;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.schema.type.form.FormItemPath;
import com.enonic.wem.api.content.schema.type.form.FormItemType;
import com.enonic.wem.api.content.schema.type.form.Input;
import com.enonic.wem.api.content.schema.type.form.MixinReference;
import com.enonic.wem.api.content.schema.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleName;

import static com.enonic.wem.api.content.schema.mixin.Mixin.newMixin;
import static com.enonic.wem.api.content.schema.type.form.Input.newInput;

public class ContentTypeStepDefs
{
    public final MockMixinFetcher mockMixinFetcher = new MockMixinFetcher();

    public final Map<String, Module> moduleByName = new HashMap<String, Module>();

    public final Map<String, ContentType> contentTypeByName = new HashMap<String, ContentType>();

    public final Map<String, Input> inputByName = new HashMap<String, Input>();

    public final Map<QualifiedMixinName, Mixin> inputMixinByQualifiedName = new HashMap<QualifiedMixinName, Mixin>();


    @Given("^a Module named (.+)$")
    public void a_module_named_name( String name )
        throws Throwable
    {
        Module module = Module.newModule().name( name ).build();
        moduleByName.put( name, module );
    }

    @Given("^a Input named (.+) of type (.+)$")
    public void a_input_named_name_of_type_type( String inputName, String inputTypeName )
        throws Throwable
    {
        Input input = newInput().name( inputName ).type( InputTypes.parse( inputTypeName ) ).build();
        inputByName.put( inputName, input );
    }

    @Given("^a InputMixin named (.+) in module (.+) with input (.+)$")
    public void a_inputMixin_named_name_in_module_module_having_input( String mixinName, String moduleName, String inputName )
        throws Throwable
    {
        Mixin inputMixin = newMixin().module( ModuleName.from( moduleName ) ).formItem( inputByName.get( inputName ) ).build();
        inputMixinByQualifiedName.put( new QualifiedMixinName( moduleName, mixinName ), inputMixin );
    }

    @Given("^a ContentType named (.+)")
    public void a_ContentType_named_name( String contentTypeName )
        throws Throwable
    {
        contentTypeByName.put( contentTypeName, ContentType.newContentType().name( contentTypeName ).build() );
    }

    @Given("^adding MixinReference named (.+) referencing InputMixin (.+) to ContentType (.+)$")
    public void adding_MixinReference_named_name_referencing_InputMixin_name_to_ContentType_myContentType( String mixinReferenceName,
                                                                                                           String mixinQualifiedName,
                                                                                                           String contentTypeName )
        throws Throwable
    {

        Mixin inputMixin = inputMixinByQualifiedName.get( new QualifiedMixinName( mixinQualifiedName ) );
        ContentType contentType = contentTypeByName.get( contentTypeName );
        contentType.form().addFormItem( MixinReference.newMixinReference( inputMixin ).name( mixinReferenceName ).build() );

        mockMixinFetcher.add( inputMixin );
        contentType.form().mixinReferencesToFormItems( mockMixinFetcher );
    }

    @When("^translating mixin references to formItems for all content types$")
    public void translating_mixin_references_to_formItems_for_all_content_types()
        throws Throwable
    {
        for ( ContentType contentType : contentTypeByName.values() )
        {
            contentType.form().mixinReferencesToFormItems( mockMixinFetcher );
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
                                  contentType.form().getFormItem( new FormItemPath( formItemPath ) ) );

            Assert.assertEquals( FormItemType.INPUT, formItemType );
        }
    }

}
