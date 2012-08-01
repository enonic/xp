package com.enonic.wem.core.content;


import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.runtime.PendingException;

public class ContentStepDefs
{
    @Given("^Value that is empty$")
    public void Value_that_is_empty()
        throws Throwable
    {
        Content content = new Content();
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @When("^content is asked if required contract is broken$")
    public void content_is_asked_if_required_contract_is_broken()
        throws Throwable
    {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @Then("^the answer should be yes$")
    public void the_answer_should_be_yes()
        throws Throwable
    {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }
}
