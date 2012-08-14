package com.enonic.wem.core.content;


import java.util.HashMap;
import java.util.Map;

import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;

import com.enonic.wem.core.content.type.ContentTypeStepDefs;

import static org.junit.Assert.*;

public class ContentStepDefs
{
    private ContentTypeStepDefs contentTypeStepDefs;

    private Map<String, Content> contentByName = new HashMap<String, Content>();

    private Object returnedValue;

    public ContentStepDefs( final ContentTypeStepDefs contentTypeStepDefs )
    {
        this.contentTypeStepDefs = contentTypeStepDefs;
    }

    @Given("^creating content named (.+) of type (.+)$")
    public void creating_content_named_name_of_type_name( String contentName, String contentTypeName )
        throws Throwable
    {

        Content content = new Content();
        content.setName( contentName );
        content.setType( contentTypeStepDefs.contentTypeByName.get( contentTypeName ) );
        contentByName.put( contentName, content );
    }

    @Given("^setting value \"([^\"]*)\" to path \"([^\"]*)\" to content named \"([^\"]*)\"$")
    public void setting_value_to_path_to_content_named( String value, String path, String contentName )
        throws Throwable
    {

        Content content = contentByName.get( contentName );
        content.setValue( path, value );
    }

    @When("^getting value \"([^\"]*)\" from content named \"([^\"]*)\"$")
    public void getting_value_from_content_named( String path, String contentName )
        throws Throwable
    {
        Content content = contentByName.get( contentName );
        returnedValue = content.getValueAsString( path );
    }

    @Then("^the returned value should be \"([^\"]*)\"$")
    public void the_return_value_should_be( String expectedValue )
        throws Throwable
    {
        assertEquals( expectedValue, returnedValue );
    }
}
