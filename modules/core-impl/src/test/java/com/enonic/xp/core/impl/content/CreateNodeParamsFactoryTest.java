package com.enonic.xp.core.impl.content;

import org.junit.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class CreateNodeParamsFactoryTest
{
    @Test
    public void toCreateNode_contentData_to_rootdataset()
        throws Exception
    {
        final PropertyTree contentData = new PropertyTree();
        contentData.setString( "test", "testValue" );

        final CreateContentTranslatorParams mycontent = CreateContentTranslatorParams.create().
            name( "mycontent" ).
            displayName( "myDisplayName" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.from( "myapplication:my-content-type" ) ).
            contentData( contentData ).
            creator( PrincipalKey.ofAnonymous() ).
            childOrder( ContentConstants.DEFAULT_CHILD_ORDER ).
            build();

        final CreateNodeParams createNode = CreateNodeParamsFactory.create( mycontent );

        assertEquals( "testValue", createNode.getData().getString( ContentPropertyNames.DATA + ".test" ) );
    }


    @Test
    public void translate_entityIndexConfig_decide_by_type_for_contentdata()
        throws Exception
    {
        final PropertyTree contentData = new PropertyTree();
        contentData.setString( "test", "testValue" );

        final CreateContentTranslatorParams mycontent = CreateContentTranslatorParams.create().
            name( "mycontent" ).
            displayName( "myDisplayName" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.from( "myapplication:my-content-type" ) ).
            contentData( contentData ).
            creator( PrincipalKey.ofAnonymous() ).
            childOrder( ContentConstants.DEFAULT_CHILD_ORDER ).
            build();

        final CreateNodeParams createNode = CreateNodeParamsFactory.create( mycontent );

        final IndexConfigDocument indexConfigDocument = createNode.getIndexConfigDocument();

        final IndexConfig configForData = indexConfigDocument.getConfigForPath( PropertyPath.from( ContentPropertyNames.DATA + ".test" ) );

        assertNotNull( configForData );
        assertEquals( true, configForData.isEnabled() );
        assertEquals( true, configForData.isDecideByType() );
    }


}