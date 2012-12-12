package com.enonic.wem.core.content.type.dao;


import javax.jcr.Node;

import org.junit.Test;

import com.enonic.wem.api.content.QualifiedSubTypeNames;
import com.enonic.wem.api.content.type.SubTypes;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.content.type.form.InputSubType;
import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.itest.AbstractJcrTest;

import static org.junit.Assert.*;

public class SubTypeDaoImplTest
    extends AbstractJcrTest
{
    private SubTypeDao subTypeDao;

    public void setupDao()
        throws Exception
    {
        subTypeDao = new SubTypeDaoImpl();
    }

    @Test
    public void createSubType()
        throws Exception
    {
        // setup
        Input myInput = Input.newInput().name( "myInput" ).label( "My input" ).type( InputTypes.TEXT_LINE ).build();
        InputSubType.Builder subTypeBuilder = InputSubType.newInputSubType().
            module( ModuleName.from( "myModule" ) ).
            displayName( "My SubType" ).input( myInput );
        SubType subType = subTypeBuilder.build();

        // exercise
        subTypeDao.createSubType( subType, session );
        commit();

        // verify
        Node subTypeNode = session.getNode( "/" + SubTypeDao.SUB_TYPES_PATH + "myModule/myInput" );
        assertNotNull( subTypeNode );
    }

    @Test
    public void updateSubType()
        throws Exception
    {
        // setup
        InputSubType subType = InputSubType.newInputSubType().
            module( ModuleName.from( "myModule" ) ).
            displayName( "My SubType" ).input(
            Input.newInput().name( "myInput" ).label( "My input" ).type( InputTypes.TEXT_LINE ).build() ).build();
        subTypeDao.createSubType( subType, session );

        // exercise
        SubTypes subTypesAfterCreate = subTypeDao.retrieveSubTypes( QualifiedSubTypeNames.from( "myModule:myInput" ), session );
        assertNotNull( subTypesAfterCreate );
        assertEquals( 1, subTypesAfterCreate.getSize() );

        InputSubType updatedSubType = InputSubType.newInputSubType().
            module( ModuleName.from( "myModule" ) ).
            displayName( "My Updated SubType" ).input(
            Input.newInput().name( "myInput" ).label( "My input" ).type( InputTypes.TEXT_AREA ).build() ).build();
        subTypeDao.updateSubType( updatedSubType, session );
        commit();

        // verify
        final SubTypes subTypesAfterUpdate = subTypeDao.retrieveSubTypes( QualifiedSubTypeNames.from( "myModule:myInput" ), session );
        assertNotNull( subTypesAfterUpdate );
        assertEquals( 1, subTypesAfterUpdate.getSize() );
        final InputSubType subType1 = (InputSubType) subTypesAfterUpdate.getFirst();
        assertEquals( "myInput", subType1.getName() );
        assertEquals( "myModule", subType1.getModuleName().toString() );
        assertEquals( "My Updated SubType", subType1.getDisplayName() );
        assertEquals( InputTypes.TEXT_AREA, subType1.getInput().getInputType() );
    }

    @Test
    public void deleteSubType()
        throws Exception
    {
        // setup
        InputSubType subType = InputSubType.newInputSubType().
            module( ModuleName.from( "myModule" ) ).
            displayName( "My SubType" ).input(
            Input.newInput().name( "myInput" ).label( "My input" ).type( InputTypes.TEXT_LINE ).build() ).build();
        subTypeDao.createSubType( subType, session );

        assertEquals( 1, subTypeDao.retrieveSubTypes( QualifiedSubTypeNames.from( "myModule:myInput" ), session ).getSize() );

        // exercise

        subTypeDao.deleteSubType( subType.getQualifiedName(), session );
        commit();

        // verify
        assertEquals( 0, subTypeDao.retrieveSubTypes( QualifiedSubTypeNames.from( "myModule:myInput" ), session ).getSize() );
    }

    @Test
    public void retrieveSubType()
        throws Exception
    {
        // setup
        InputSubType subType = InputSubType.newInputSubType().
            module( ModuleName.from( "myModule" ) ).
            displayName( "My SubType" ).input(
            Input.newInput().name( "myInput" ).label( "My input" ).type( InputTypes.TEXT_LINE ).build() ).build();

        subTypeDao.createSubType( subType, session );

        // exercise
        final SubTypes subTypes = subTypeDao.retrieveSubTypes( QualifiedSubTypeNames.from( "myModule:myInput" ), session );
        commit();

        // verify
        assertNotNull( subTypes );
        assertEquals( 1, subTypes.getSize() );
        InputSubType subType1 = (InputSubType) subTypes.getFirst();
        assertEquals( "myInput", subType1.getName() );
        assertEquals( "myModule", subType1.getModuleName().toString() );
        assertEquals( "My SubType", subType1.getDisplayName() );
        assertEquals( "myInput", subType1.getInput().getName() );
        assertEquals( "My input", subType1.getInput().getLabel() );
        assertEquals( InputTypes.TEXT_LINE, subType1.getInput().getInputType() );
    }

    @Test
    public void retrieveAllContentTypes()
        throws Exception
    {
        // setup
        InputSubType subType1 = InputSubType.newInputSubType().
            module( ModuleName.from( "myModule" ) ).
            displayName( "My SubType 1" ).input(
            Input.newInput().name( "myInput" ).label( "My input 1" ).type( InputTypes.TEXT_LINE ).build() ).build();
        subTypeDao.createSubType( subType1, session );

        InputSubType subType2 = InputSubType.newInputSubType().
            module( ModuleName.from( "otherModule" ) ).
            displayName( "My SubType 2" ).input(
            Input.newInput().name( "myInput" ).label( "My input 2" ).type( InputTypes.DATE ).build() ).build();
        subTypeDao.createSubType( subType2, session );

        // exercise
        final SubTypes subTypes = subTypeDao.retrieveAllSubTypes( session );
        commit();

        // verify
        assertNotNull( subTypes );
        assertEquals( 2, subTypes.getSize() );
        InputSubType actualSubType1 = (InputSubType) subTypes.getSubType( new QualifiedSubTypeName( "myModule:myInput" ) );
        InputSubType actualSubType2 = (InputSubType) subTypes.getSubType( new QualifiedSubTypeName( "otherModule:myInput" ) );

        assertEquals( "myInput", actualSubType1.getName() );
        assertEquals( "myModule", actualSubType1.getModuleName().toString() );
        assertEquals( "My SubType 1", actualSubType1.getDisplayName() );
        assertEquals( InputTypes.TEXT_LINE, actualSubType1.getInput().getInputType() );

        assertEquals( "myInput", actualSubType2.getName() );
        assertEquals( "otherModule", actualSubType2.getModuleName().toString() );
        assertEquals( "My SubType 2", actualSubType2.getDisplayName() );
        assertEquals( InputTypes.DATE, actualSubType2.getInput().getInputType() );
    }

}
