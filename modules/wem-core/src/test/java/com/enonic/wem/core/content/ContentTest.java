package com.enonic.wem.core.content;


import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.component.ComponentSet;
import com.enonic.wem.api.content.type.component.ComponentSetSubType;
import com.enonic.wem.api.content.type.component.FieldSet;
import com.enonic.wem.api.content.type.component.Input;
import com.enonic.wem.api.content.type.component.InputSubType;
import com.enonic.wem.api.content.type.component.InvalidDataException;
import com.enonic.wem.api.content.type.component.MockSubTypeFetcher;
import com.enonic.wem.api.content.type.component.SubTypeReference;
import com.enonic.wem.api.content.type.component.inputtype.InputTypes;
import com.enonic.wem.api.content.type.component.inputtype.SingleSelectorConfig;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.component.ComponentSet.newComponentSet;
import static com.enonic.wem.api.content.type.component.ComponentSetSubType.newComponentSetSubType;
import static com.enonic.wem.api.content.type.component.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.component.Input.newInput;
import static com.enonic.wem.api.content.type.component.InputSubType.newInputSubType;
import static com.enonic.wem.api.content.type.component.SubTypeReference.newSubTypeReference;
import static com.enonic.wem.api.content.type.component.inputtype.SingleSelectorConfig.newSingleSelectorConfig;
import static com.enonic.wem.api.module.Module.newModule;
import static org.junit.Assert.*;

public class ContentTest
{

    @Test
    public void singleSelector()
    {
        ContentType contentType = new ContentType();
        SingleSelectorConfig singleSelectorConfig =
            newSingleSelectorConfig().type( SingleSelectorConfig.SelectorType.DROPDOWN ).addOption( "Option 1", "o1" ).addOption(
                "Option 2", "o2" ).build();
        Input mySingleSelector =
            newInput().name( "mySingleSelector" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build();
        contentType.addComponent( mySingleSelector );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "mySingleSelector", "o1" );

        assertEquals( "o1", content.getData( "mySingleSelector" ).getValue() );
    }

    @Test
    public void multiple_textlines()
    {
        ContentType contentType = new ContentType();
        contentType.addComponent( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );
        contentType.addComponent( newInput().name( "myMultipleTextLine" ).type( InputTypes.TEXT_LINE ).multiple( true ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myTextLine", "A single line" );
        content.setData( "myMultipleTextLine[0]", "First line" );
        content.setData( "myMultipleTextLine[1]", "Second line" );

        assertEquals( "A single line", content.getData( "myTextLine" ).getValue() );
        assertEquals( "First line", content.getData( "myMultipleTextLine[0]" ).getValue() );
        assertEquals( "Second line", content.getData( "myMultipleTextLine[1]" ).getValue() );
    }

    @Test
    public void tags()
    {
        ContentType contentType = new ContentType();
        contentType.addComponent( newInput().name( "myTags" ).type( InputTypes.TAGS ).build() );

        // TODO: Are'nt tags best stored as an array? A global mixin multiple textline?
        Content content = new Content();
        content.setType( contentType );
        content.setData( "myTags", "A line of text" );

        assertEquals( "A line of text", content.getData( "myTags" ).getValue() );
    }

    @Test
    public void tags_using_subType()
    {
        Module module = newModule().name( "system" ).build();
        Input input = newInput().name( "tags" ).label( "Tags" ).type( InputTypes.TEXT_LINE ).multiple( true ).build();
        InputSubType inputSubType = newInputSubType().module( module ).input( input ).build();
        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( inputSubType );

        ContentType contentType = new ContentType();
        contentType.addComponent(
            SubTypeReference.newSubTypeReference().name( "myTags" ).subType( "system:tags" ).type( InputSubType.class ).build() );
        contentType.subTypeReferencesToComponents( subTypeFetcher );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myTags[0]", "Java" );
        content.setData( "myTags[1]", "XML" );
        content.setData( "myTags[2]", "JSON" );

        assertEquals( "Java", content.getData( "myTags" ).getValue() );
    }

    @Test
    public void phone()
    {
        ContentType contentType = new ContentType();
        contentType.addComponent( newInput().name( "myPhone" ).type( InputTypes.PHONE ).required( true ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "myPhone", "98327891" );

        assertEquals( "98327891", content.getData( "myPhone" ).getValue() );
    }

    @Test
    public void componentSet()
    {
        ContentType contentType = new ContentType();
        contentType.addComponent( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).required( true ).build() );

        ComponentSet componentSet = newComponentSet().name( "personalia" ).build();
        contentType.addComponent( componentSet );
        componentSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "name", "Ola Nordmann" );
        content.setData( "personalia.eyeColour", "Blue" );
        content.setData( "personalia.hairColour", "Blonde" );

        assertEquals( "Ola Nordmann", content.getData( "name" ).getValue() );
        assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getValue() );
        assertEquals( "Blonde", content.getData( "personalia.hairColour" ).getValue() );
    }

    @Test
    public void multiple_subtype()
    {
        ContentType contentType = new ContentType();
        Input nameInput = newInput().name( "name" ).type( InputTypes.TEXT_LINE ).required( true ).build();
        contentType.addComponent( nameInput );

        ComponentSet componentSet = newComponentSet().name( "personalia" ).multiple( true ).build();
        contentType.addComponent( componentSet );
        componentSet.add( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "name", "Norske" );
        content.setData( "personalia[0].name", "Ola Nordmann" );
        content.setData( "personalia[0].eyeColour", "Blue" );
        content.setData( "personalia[0].hairColour", "Blonde" );
        content.setData( "personalia[1].name", "Kari Trestakk" );
        content.setData( "personalia[1].eyeColour", "Green" );
        content.setData( "personalia[1].hairColour", "Brown" );

        assertEquals( "Norske", content.getData( "name" ).getValue() );
        assertEquals( "Ola Nordmann", content.getData( "personalia[0].name" ).getValue() );
        assertEquals( "Blue", content.getData( "personalia[0].eyeColour" ).getValue() );
        assertEquals( "Blonde", content.getData( "personalia[0].hairColour" ).getValue() );
        assertEquals( "Kari Trestakk", content.getData( "personalia[1].name" ).getValue() );
        assertEquals( "Green", content.getData( "personalia[1].eyeColour" ).getValue() );
        assertEquals( "Brown", content.getData( "personalia[1].hairColour" ).getValue() );
    }

    @Test
    public void unstructured()
    {
        Content content = new Content();
        content.setData( "firstName", "Thomas", DataTypes.TEXT );
        content.setData( "description", "Grew up in Noetteveien", DataTypes.HTML_PART );
        content.setData( "child[0].name", "Joachim" );
        content.setData( "child[0].age", "9" );
        content.setData( "child[0].features.eyeColour", "Blue" );
        content.setData( "child[0].features.hairColour", "Blonde" );
        content.setData( "child[1].name", "Madeleine" );
        content.setData( "child[1].age", "7" );
        content.setData( "child[1].features.eyeColour", "Brown" );
        content.setData( "child[1].features.hairColour", "Black" );

        assertEquals( "Thomas", content.getData( "firstName" ).getValue() );
        assertEquals( DataTypes.TEXT, content.getData( "firstName" ).getDataType() );
        assertEquals( DataTypes.HTML_PART, content.getData( "description" ).getDataType() );
        assertEquals( "Joachim", content.getData( "child[0].name" ).getValue() );
        assertEquals( "9", content.getData( "child[0].age" ).getValue() );
        assertEquals( "Blue", content.getData( "child[0].features.eyeColour" ).getValue() );
        assertEquals( "Blonde", content.getData( "child[0].features.hairColour" ).getValue() );
        assertEquals( "Madeleine", content.getData( "child[1].name" ).getValue() );
        assertEquals( "7", content.getData( "child[1].age" ).getValue() );
        assertEquals( "Brown", content.getData( "child[1].features.eyeColour" ).getValue() );
        assertEquals( "Black", content.getData( "child[1].features.hairColour" ).getValue() );
    }

    @Test(expected = InvalidDataException.class)
    public void setData_given_invalid_value_when_dataType_is_geographical_coordinate_then_exception()
    {
        Content content = new Content();
        DataSet value = DataSet.newDataSet().set( "latitude", 0.0, DataTypes.DECIMAL_NUMBER ).set( "longitude", 181.0,
                                                                                                   DataTypes.DECIMAL_NUMBER ).build();
        content.setData( "myGeoLocation", value, DataTypes.GEOGRAPHIC_COORDINATE );
    }

    @Test
    public void unstructured_getEntries()
    {
        Content content = new Content();
        content.setData( "child[0].name", "Joachim" );
        content.setData( "child[0].age", "9" );
        content.setData( "child[0].features.eyeColour", "Blue" );
        content.setData( "child[0].features.hairColour", "Blonde" );
        content.setData( "child[1].name", "Madeleine" );
        content.setData( "child[1].age", "7" );
        content.setData( "child[1].features.eyeColour", "Brown" );
        content.setData( "child[1].features.hairColour", "Black" );

        DataSet child0 = content.getDataSet( "child[0]" );
        assertEquals( "Joachim", child0.getData( "name" ).getValue() );
        assertEquals( "9", child0.getData( "age" ).getValue() );
        assertEquals( "Blue", child0.getData( "features.eyeColour" ).getValue() );

        DataSet child1 = content.getDataSet( "child[1]" );
        assertEquals( "Madeleine", child1.getData( "name" ).getValue() );
        assertEquals( "7", child1.getData( "age" ).getValue() );
        assertEquals( "Brown", child1.getData( "features.eyeColour" ).getValue() );
    }

    @Test
    public void structured_getEntries()
    {
        ComponentSet child = newComponentSet().name( "child" ).multiple( true ).build();
        child.add( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() );
        child.add( newInput().name( "age" ).type( InputTypes.TEXT_LINE ).build() );
        ComponentSet features = newComponentSet().name( "features" ).multiple( false ).build();
        features.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        features.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );
        child.add( features );
        ContentType contentType = new ContentType();
        contentType.addComponent( child );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "child[0].name", "Joachim" );
        content.setData( "child[0].age", "9" );
        content.setData( "child[0].features.eyeColour", "Blue" );
        content.setData( "child[0].features.hairColour", "Blonde" );
        content.setData( "child[1].name", "Madeleine" );
        content.setData( "child[1].age", "7" );
        content.setData( "child[1].features.eyeColour", "Brown" );
        content.setData( "child[1].features.hairColour", "Black" );

        DataSet child0 = content.getDataSet( "child[0]" );
        assertEquals( "Joachim", child0.getData( "name" ).getValue() );
        assertEquals( "9", child0.getData( "age" ).getValue() );
        assertEquals( "Blue", child0.getData( "features.eyeColour" ).getValue() );

        DataSet child1 = content.getDataSet( "child[1]" );
        assertEquals( "Madeleine", child1.getData( "name" ).getValue() );
        assertEquals( "7", child1.getData( "age" ).getValue() );
        assertEquals( "Brown", child1.getData( "features.eyeColour" ).getValue() );
    }

    @Test
    public void given_unstructured_content_when_getting_values_then_they_are_returned()
    {
        // setup
        Content content = new Content();
        content.setData( "name", "Thomas" );
        content.setData( "personalia.eyeColour", "Blue", DataTypes.TEXT );
        content.setData( "personalia.hairColour", "Blonde" );

        assertEquals( DataTypes.TEXT, content.getData( "personalia.eyeColour" ).getDataType() );
        assertEquals( "Blue", content.getData( "personalia.eyeColour" ).getValue() );
        assertEquals( "personalia.eyeColour", content.getData( "personalia.eyeColour" ).getPath().toString() );
    }

    @Test
    public void subTypes()
    {
        Module module = newModule().name( "myModule" ).build();

        InputSubType postalCodeSubType =
            newInputSubType().module( module ).input( newInput().name( "postalCode" ).type( InputTypes.TEXT_LINE ).build() ).build();
        InputSubType countrySubType = newInputSubType().module( module ).input(
            newInput().name( "country" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig(
                newSingleSelectorConfig().typeDropdown().addOption( "Norway", "NO" ).build() ).build() ).build();

        ComponentSetSubType addressSubType = newComponentSetSubType().module( module ).componentSet(
            newComponentSet().name( "address" ).add( newInput().name( "street" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newSubTypeReference( postalCodeSubType ).name( "postalCode" ).build() ).add(
                newInput().name( "postalPlace" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newSubTypeReference( countrySubType ).name( "country" ).build() ).build() ).build();

        ContentType contentType = new ContentType();
        contentType.setName( "person" );
        contentType.addComponent( newInput().type( InputTypes.TEXT_LINE ).name( "name" ).build() );
        contentType.addComponent( newSubTypeReference( addressSubType ).name( "address" ).build() );

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( postalCodeSubType );
        subTypeFetcher.add( countrySubType );
        subTypeFetcher.add( addressSubType );
        contentType.subTypeReferencesToComponents( subTypeFetcher );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "name", "Ola Normann" );
        content.setData( "address.street", "Bakkebygrenda 1" );
        content.setData( "address.postalCode", "2676" );
        content.setData( "address.postalPlace", "Heidal" );
        content.setData( "address.country", "NO" );

        assertEquals( "Ola Normann", content.getValueAsString( "name" ) );
        assertEquals( "Bakkebygrenda 1", content.getValueAsString( "address.street" ) );
        assertEquals( "2676", content.getValueAsString( "address.postalCode" ) );
        assertEquals( "Heidal", content.getValueAsString( "address.postalPlace" ) );
        assertEquals( "NO", content.getValueAsString( "address.country" ) );
    }

    @Test
    public void subTypes_multiple()
    {
        Module module = newModule().name( "myModule" ).build();

        ComponentSetSubType addressSubType = newComponentSetSubType().module( module ).componentSet(
            newComponentSet().name( "address" ).multiple( true ).add( newInput().type( InputTypes.TEXT_LINE ).name( "label" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "street" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "postalCode" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "postalPlace" ).build() ).add(
                newInput().type( InputTypes.TEXT_LINE ).name( "country" ).build() ).build() ).build();

        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        contentType.addComponent( newSubTypeReference( addressSubType ).name( "address" ).build() );

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( addressSubType );
        contentType.subTypeReferencesToComponents( subTypeFetcher );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "address[0].label", "Home" );
        content.setData( "address[0].street", "Bakkebygrenda 1" );
        content.setData( "address[0].postalCode", "2676" );
        content.setData( "address[0].postalPlace", "Heidal" );
        content.setData( "address[0].country", "NO" );
        content.setData( "address[1].label", "Cabin" );
        content.setData( "address[1].street", "Heia" );
        content.setData( "address[1].postalCode", "2676" );
        content.setData( "address[1].postalPlace", "Gjende" );
        content.setData( "address[1].country", "NO" );

        assertEquals( "Home", content.getValueAsString( "address[0].label" ) );
        assertEquals( "Bakkebygrenda 1", content.getValueAsString( "address[0].street" ) );
        assertEquals( "2676", content.getValueAsString( "address[0].postalCode" ) );
        assertEquals( "Heidal", content.getValueAsString( "address[0].postalPlace" ) );
        assertEquals( "NO", content.getValueAsString( "address[0].country" ) );

        assertEquals( "Cabin", content.getValueAsString( "address[1].label" ) );
        assertEquals( "Heia", content.getValueAsString( "address[1].street" ) );
        assertEquals( "2676", content.getValueAsString( "address[1].postalCode" ) );
        assertEquals( "Gjende", content.getValueAsString( "address[1].postalPlace" ) );
        assertEquals( "NO", content.getValueAsString( "address[1].country" ) );
    }

    @Test
    public void trying_to_set_data_to_a_fieldSetSubType_when_subType_is_missing()
    {
        ContentType contentType = new ContentType();
        contentType.addComponent( newInput().type( InputTypes.TEXT_LINE ).name( "name" ).build() );
        contentType.addComponent(
            SubTypeReference.newSubTypeReference().name( "address" ).typeInput().subType( "myModule:myAddressSubType" ).build() );

        contentType.subTypeReferencesToComponents( new MockSubTypeFetcher() );

        Content content = new Content();
        content.setType( contentType );
        content.setData( "name", "Ola Normann" );
        try
        {
            content.setData( "address.street", "Norvegen 99" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "Component at path [address.street] expected to be of type FieldSet: REFERENCE", e.getMessage() );
        }
    }

    @Test
    public void layout()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.setName( "test" );
        contentType.addComponent( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() );
        FieldSet personalia = newFieldSet().label( "Personalia" ).name( "personalia" ).add(
            newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() ).add(
            newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() ).build();
        FieldSet tatoos = newFieldSet().label( "Characteristics" ).name( "characteristics" ).add(
            newInput().name( "tattoo" ).type( InputTypes.TEXT_LINE ).multiple( true ).build() ).add(
            newInput().name( "scar" ).type( InputTypes.TEXT_LINE ).multiple( true ).build() ).build();
        personalia.addComponent( tatoos );
        contentType.addComponent( personalia );

        Content content = new Content();
        content.setType( contentType );

        // exercise
        content.setData( "name", "Ola Norman" );
        content.setData( "eyeColour", "Blue" );
        content.setData( "hairColour", "Blonde" );
        content.setData( "tattoo[0]", "Skull on left arm" );
        content.setData( "tattoo[1]", "Mothers name on right arm" );
        content.setData( "scar[0]", "Chin" );

        // verify
        assertEquals( "Ola Norman", content.getValueAsString( "name" ) );
        assertEquals( "Blue", content.getValueAsString( "eyeColour" ) );
        assertEquals( "Blonde", content.getValueAsString( "hairColour" ) );
        assertEquals( "Skull on left arm", content.getValueAsString( "tattoo[0]" ) );
        assertEquals( "Mothers name on right arm", content.getValueAsString( "tattoo[1]" ) );
        assertEquals( "Chin", content.getValueAsString( "scar[0]" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void given_array_when_setting_data_of_another_type_to_array_then_exception_is_thrown()
    {
        // setup
        Content content = new Content();
        content.setData( "myData", "Value 1", DataTypes.TEXT );

        // exercise
        content.setData( "myData[1]", new DateMidnight( 2000, 1, 1 ), DataTypes.DATE );
    }
}
