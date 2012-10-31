package com.enonic.wem.api.content.type.component;


import org.junit.Test;

import com.enonic.wem.api.content.type.component.inputtype.InputTypes;

import static com.enonic.wem.api.content.type.component.Input.newInput;
import static org.junit.Assert.*;

public class ComponentsTest
{
    @Test
    public void getConfig()
    {
        Components components = new Components();
        ComponentSet componentSet = ComponentSet.newBuilder().name( "personalia" ).build();
        components.add( componentSet );
        componentSet.addInput( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.addInput( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise & verify
        HierarchicalComponent personaliaConfig = components.getHierarchicalComponent( new ComponentPath( "personalia" ).getLastElement() );
        assertEquals( "personalia", personaliaConfig.getPath().toString() );
    }

    @Test
    public void getConfig2()
    {
        Components components = new Components();
        ComponentSet componentSet = ComponentSet.newBuilder().name( "personalia" ).label( "Personalia" ).build();
        components.add( componentSet );
        componentSet.addInput( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        componentSet.addInput( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise & verify
        HierarchicalComponent personaliaEyeColourConfig = componentSet.getComponents().getHierarchicalComponent( "eyeColour" );
        assertEquals( "personalia.eyeColour", personaliaEyeColourConfig.getPath().toString() );
    }

    @Test
    public void toString_with_two_fields()
    {
        Components components = new Components();
        components.add( newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() );
        components.add( newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() );

        // exercise & verify
        assertEquals( "eyeColour, hairColour", components.toString() );
    }

    @Test
    public void toString_with_layout()
    {
        Components components = new Components();
        components.add( newInput().name( "name" ).type( InputTypes.TEXT_LINE ).build() );
        components.add( FieldSet.newFieldSet().label( "Layout" ).name( "layout" ).add(
            newInput().name( "eyeColour" ).type( InputTypes.TEXT_LINE ).build() ).add(
            newInput().name( "hairColour" ).type( InputTypes.TEXT_LINE ).build() ).build() );

        // exercise & verify
        assertEquals( "name, layout{eyeColour, hairColour}", components.toString() );
    }
}
