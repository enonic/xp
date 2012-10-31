package com.enonic.wem.core.content.type;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeSerializer;
import com.enonic.wem.api.content.type.component.ComponentPath;
import com.enonic.wem.api.content.type.component.ComponentSet;
import com.enonic.wem.api.content.type.component.ComponentSetSubType;
import com.enonic.wem.api.content.type.component.Components;
import com.enonic.wem.api.content.type.component.FieldSet;
import com.enonic.wem.api.content.type.component.Input;
import com.enonic.wem.api.content.type.component.MockSubTypeFetcher;
import com.enonic.wem.api.content.type.component.SubTypeReference;
import com.enonic.wem.api.content.type.component.inputtype.InputTypes;
import com.enonic.wem.api.content.type.component.inputtype.SingleSelectorConfig;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.content.type.component.ComponentSet.newComponentSet;
import static com.enonic.wem.api.content.type.component.ComponentSetSubType.newComponentSetSubType;
import static com.enonic.wem.api.content.type.component.FieldSet.newFieldSet;
import static com.enonic.wem.api.content.type.component.Input.newInput;
import static com.enonic.wem.api.content.type.component.SubTypeReference.newSubTypeReference;
import static com.enonic.wem.api.module.Module.newModule;
import static org.junit.Assert.*;


public abstract class AbstractContentTypeSerializerTest
{
    private static final Module myModule = newModule().name( "myModule" ).build();

    private ContentTypeSerializer serializer;

    abstract ContentTypeSerializer getSerializer();

    @Before
    public void before()
    {
        this.serializer = getSerializer();
    }

    @Test
    public void parse_all_types()
    {
        SingleSelectorConfig singleSelectorConfig =
            SingleSelectorConfig.newSingleSelectorConfig().typeDropdown().addOption( "myOption 1", "o1" ).addOption( "myOption 2",
                                                                                                                     "o2" ).build();

        ContentType contentType = new ContentType();
        contentType.setName( "MyContentType" );
        contentType.setModule( myModule );
        Components components = new Components();
        contentType.setComponents( components );
        components.add( newInput().name( "myDate" ).type( InputTypes.DATE ).build() );
        components.add(
            newInput().name( "mySingleSelector" ).type( InputTypes.SINGLE_SELECTOR ).inputTypeConfig( singleSelectorConfig ).build() );
        components.add( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );
        components.add( newInput().name( "myTextArea" ).type( InputTypes.TEXT_AREA ).build() );
        components.add( newInput().name( "myPhone" ).type( InputTypes.PHONE ).build() );
        components.add( newInput().name( "myXml" ).type( InputTypes.XML ).build() );

        ComponentSet componentSet = ComponentSet.newBuilder().name( "mySet" ).label( "My set" ).build();
        components.add( componentSet );
        componentSet.addInput( newInput().name( "myText1" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.addInput( newInput().name( "myText2" ).occurrences( 1, 3 ).type( InputTypes.TEXT_LINE ).build() );

        String serialized = toString( contentType );

        // exercise
        ContentType actualContentType = toContentType( serialized );

        // verify
        assertNotNull( actualContentType );
        Components actualComponents = actualContentType.getComponents();

        assertNotNull( actualComponents );
        assertEquals( 7, actualComponents.size() );

        assertNotNull( actualComponents.getComponent( new ComponentPath( "myDate" ).getLastElement() ) );
        assertNotNull( actualComponents.getComponent( new ComponentPath( "mySingleSelector" ).getLastElement() ) );
        assertNotNull( actualComponents.getComponent( new ComponentPath( "myTextLine" ).getLastElement() ) );
        assertNotNull( actualComponents.getComponent( new ComponentPath( "myTextArea" ).getLastElement() ) );
        assertNotNull( actualComponents.getComponent( new ComponentPath( "myPhone" ).getLastElement() ) );
        assertNotNull( actualComponents.getComponent( new ComponentPath( "myXml" ).getLastElement() ) );
        assertNotNull( actualComponents.getComponent( new ComponentPath( "mySet" ).getLastElement() ) );
        assertNotNull( actualComponents.getInput( new ComponentPath( "mySet.myText1" ) ) );
        assertNotNull( actualComponents.getInput( new ComponentPath( "mySet.myText2" ) ) );
    }

    @Test
    public void parse_subType()
    {
        // setup
        Module module = newModule().name( "myModule" ).build();

        ComponentSetSubType subType = newComponentSetSubType().module( module ).componentSet(
            newComponentSet().name( "address" ).add( newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "postalNo" ).label( "Postal No" ).type( InputTypes.TEXT_LINE ).build() ).add(
                newInput().name( "country" ).label( "Country" ).type( InputTypes.TEXT_LINE ).build() ).build() ).build();

        ContentType cty = new ContentType();
        cty.setModule( myModule );
        cty.addComponent( newInput().name( "myTextLine" ).type( InputTypes.TEXT_LINE ).build() );
        cty.addComponent( newSubTypeReference( subType ).name( "home" ).build() );
        cty.addComponent( newSubTypeReference( subType ).name( "cabin" ).build() );

        MockSubTypeFetcher subTypeFetcher = new MockSubTypeFetcher();
        subTypeFetcher.add( subType );

        String serialized = toString( cty );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify references
        assertEquals( SubTypeReference.class, parsedContentType.getComponents().getComponent( "home" ).getClass() );
        assertEquals( SubTypeReference.class, parsedContentType.getComponents().getComponent( "cabin" ).getClass() );

        // verify items past the reference is null
        assertEquals( null, parsedContentType.getComponents().getComponent( "home.street" ) );
    }

    @Test
    public void given_content_type_with_componentSet_inside_componentSet_and_component_in_both_when_parse_then_paths_are_correct()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        Input myInnerInput = newInput().name( "my-inner-input" ).type( InputTypes.TEXT_LINE ).build();
        ComponentSet myInnerSet = newComponentSet().name( "my-inner-set" ).add( myInnerInput ).build();
        Input myOuterInput = newInput().name( "my-outer-input" ).type( InputTypes.TEXT_LINE ).build();
        ComponentSet myOuterSet = newComponentSet().name( "my-outer-set" ).add( myOuterInput ).add( myInnerSet ).build();
        contentType.addComponent( myOuterSet );

        String serialized = toString( contentType );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertEquals( "my-outer-set", parsedContentType.getComponentSet( "my-outer-set" ).getPath().toString() );
        assertEquals( "my-outer-set.my-outer-input", parsedContentType.getInput( "my-outer-set.my-outer-input" ).getPath().toString() );
        assertEquals( "my-outer-set.my-inner-set", parsedContentType.getComponentSet( "my-outer-set.my-inner-set" ).getPath().toString() );
        assertEquals( "my-outer-set.my-inner-set.my-inner-input",
                      parsedContentType.getInput( "my-outer-set.my-inner-set.my-inner-input" ).getPath().toString() );
    }

    @Test
    public void given_layout_with_component_inside_when_parsed_it_exists()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "test" );
        FieldSet layout = newFieldSet().label( "Label" ).name( "fieldSet" ).add(
            newInput().name( "myComponent" ).type( InputTypes.TEXT_LINE ).build() ).build();
        contentType.addComponent( layout );

        String serialized = toString( contentType );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertEquals( "myComponent", parsedContentType.getInput( "myComponent" ).getPath().toString() );
        assertNotNull( parsedContentType.getComponents().getComponent( "fieldSet" ) );
        assertEquals( FieldSet.class, parsedContentType.getComponents().getComponent( "fieldSet" ).getClass() );
    }

    @Test
    public void given_component_with_validationRegex_when_parsed_then_it_exists()
    {
        // setup
        ContentType contentType = new ContentType();
        contentType.setModule( myModule );
        contentType.setName( "test" );
        contentType.addComponent( newInput().name( "myText" ).type( InputTypes.TEXT_LINE ).validationRegexp( "a*c" ).build() );
        String serialized = toString( contentType );

        // exercise
        ContentType parsedContentType = toContentType( serialized );

        // verify
        assertEquals( "a*c", parsedContentType.getInput( "myText" ).getValidationRegexp().toString() );
    }

    private ContentType toContentType( final String serialized )
    {
        return serializer.toContentType( serialized );
    }

    private String toString( final ContentType type )
    {
        String serialized = getSerializer().toString( type );
        System.out.println( serialized );
        return serialized;
    }
}
