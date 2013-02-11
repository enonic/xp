package com.enonic.wem.core.content.relationshiptype;


import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.support.serializer.XmlParsingException;
import com.enonic.wem.core.util.JdomHelper;

public class RelationshipTypeXmlSerializer
{
    private final JdomHelper jdomHelper = new JdomHelper();

    private boolean prettyPrint = true;

    public RelationshipTypeXmlSerializer prettyPrint( boolean value )
    {
        this.prettyPrint = value;
        return this;
    }

    public String toString( RelationshipType type )
    {
        return this.jdomHelper.serialize( toJDomDocument( type ), this.prettyPrint );
    }

    public Document toJDomDocument( RelationshipType type )
    {
        final Element typeEl = new Element( "relationship-type" );
        generate( type, typeEl );
        return new Document( typeEl );
    }


    private void generate( final RelationshipType type, final Element typeEl )
    {
        typeEl.addContent( new Element( "name" ).setText( type.getName() ) );
        typeEl.addContent( new Element( "display-name" ).setText( type.getDisplayName() ) );
        typeEl.addContent( new Element( "module" ).setText( type.getModuleName().toString() ) );

        typeEl.addContent( new Element( "from-semantic" ).setText( type.getFromSemantic() ) );
        typeEl.addContent( new Element( "to-semantic" ).setText( type.getFromSemantic() ) );


        final Element allowedFromTypes = new Element( "allowed-from-types" );
        for ( QualifiedContentTypeName allowedFromType : type.getAllowedFromTypes() )
        {
            allowedFromTypes.addContent( new Element( "allowed-from-type" ).setText( allowedFromType.toString() ) );
        }
        typeEl.addContent( allowedFromTypes );


        final Element allowedToTypes = new Element( "allowed-to-types" );
        for ( QualifiedContentTypeName allowedToType : type.getAllowedToTypes() )
        {
            allowedToTypes.addContent( new Element("allowed-to-type").setText( allowedToType.toString() ));
        }
        typeEl.addContent( allowedToTypes );
    }

    public RelationshipType toRelationshipType( String xml )
        throws XmlParsingException
    {
        try
        {
            final Document document = this.jdomHelper.parse( xml );
            return parse( document.getRootElement() );
        }
        catch ( JDOMException e )
        {
            throw new XmlParsingException( "Failed to read XML (JDOMException)", e );
        }
        catch ( IOException e )
        {
            throw new XmlParsingException( "Failed to read XML (IOException)", e );
        }
    }

    private RelationshipType parse( final Element relationshipTypeEl )
        throws IOException
    {
        final String module = relationshipTypeEl.getChildText( "module" );
        final String name = relationshipTypeEl.getChildText( "name" );
        final String displayName = relationshipTypeEl.getChildText( "display-name" );

        final String fromSemantic = relationshipTypeEl.getChildText( "from-semantic" );
        final String toSemantic = relationshipTypeEl.getChildText( "to-semantic" );

        final RelationshipType.Builder relationshipTypeBuilder = RelationshipType.newRelationshipType().
            name( name ).
            module( ModuleName.from( module ) ).
            displayName( displayName ).
            fromSemantic( fromSemantic ).
            toSemantic( toSemantic );

        for ( final Element element : getChildren( relationshipTypeEl, "allowed-from-types" ) )
        {
            relationshipTypeBuilder.addAllowedFromType( new QualifiedContentTypeName( element.getValue() ) );
        }

        for ( final Element element : getChildren( relationshipTypeEl, "allowed-to-types" ) )
        {
            relationshipTypeBuilder.addAllowedToType( new QualifiedContentTypeName( element.getValue() ) );
        }

        return relationshipTypeBuilder.build();
    }



    @SuppressWarnings({"unchecked"})
    private List<Element> getChildren( final Element element, final String name )
    {
        final List list = element.getChildren( name );
        if ( list == null )
        {
            return Collections.emptyList();
        }
        return list;
    }
}
