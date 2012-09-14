package com.enonic.wem.api.userstore.config;

import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.enonic.wem.api.exception.InvalidUserStoreConfigException;

public final class UserStoreConfigParser
{
    private final static String ROOT_ELEMENT_NAME = "config";

    private final static String USER_FIELDS_ELEMENT_NAME = "user-fields";

    private final SAXBuilder builder;

    public UserStoreConfigParser()
    {
        this.builder = new SAXBuilder();
    }

    public UserStoreConfig parseXml( final String xml )
        throws Exception
    {
        final Document doc = this.builder.build( new StringReader( xml ) );
        return parseXml( doc );
    }

    public UserStoreConfig parseXml( final Document xml )
        throws Exception
    {
        UserStoreConfig userStoreConfig = new UserStoreConfig();
        Element root = xml.getRootElement();
        if ( !ROOT_ELEMENT_NAME.equals( root.getName() ) )
        {
            throw new InvalidUserStoreConfigException( "Illegal root element:  {0}; must be: {1}", root.getName(), ROOT_ELEMENT_NAME );
        }
        Element userFields = root.getChild( USER_FIELDS_ELEMENT_NAME );
        if ( userFields == null )
        {
            throw new InvalidUserStoreConfigException( "Cannot find '{0}' element", USER_FIELDS_ELEMENT_NAME );
        }
        List<Element> children = userFields.getChildren();
        for ( Element child : children )
        {
            UserStoreFieldConfig userStoreFieldConfig = parseUserFieldElement( child );
            userStoreConfig.addField( userStoreFieldConfig );
        }

        return userStoreConfig;
    }

    private UserStoreFieldConfig parseUserFieldElement( Element userField )
    {
        UserStoreFieldConfig userStoreFieldConfig = new UserStoreFieldConfig( userField.getName() );
        String iso = userField.getAttributeValue( "iso" );
        String readOnly = userField.getAttributeValue( "readonly" );
        String required = userField.getAttributeValue( "required" );
        String remote = userField.getAttributeValue( "remote" );
        userStoreFieldConfig.setIso( iso != null ? Boolean.valueOf( iso ) : true );
        userStoreFieldConfig.setReadOnly( Boolean.valueOf( readOnly ) );
        userStoreFieldConfig.setRequired( Boolean.valueOf( required ) );
        userStoreFieldConfig.setRemote( Boolean.valueOf( remote ) );
        if ( userStoreFieldConfig.isRequired() && userStoreFieldConfig.isReadOnly() )
        {
            throw new InvalidUserStoreConfigException(
                "Illegal field config in {0}: field cannot be required and readonly in the same time", userStoreFieldConfig.getName() );
        }
        return userStoreFieldConfig;
    }
}
