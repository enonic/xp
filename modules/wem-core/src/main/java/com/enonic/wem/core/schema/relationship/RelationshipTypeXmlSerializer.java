package com.enonic.wem.core.schema.relationship;


import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.support.serializer.XmlParsingException;
import com.enonic.wem.core.support.util.JdomHelper;

public class RelationshipTypeXmlSerializer
    implements RelationshipTypeSerializer
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

        typeEl.addContent( new Element( "from-semantic" ).setText( type.getFromSemantic() ) );
        typeEl.addContent( new Element( "to-semantic" ).setText( type.getToSemantic() ) );

        final Element allowedFromTypes = new Element( "allowed-from-types" );
        for ( ContentTypeName allowedFromType : type.getAllowedFromTypes() )
        {
            allowedFromTypes.addContent( new Element( "content-type" ).setText( allowedFromType.toString() ) );
        }
        typeEl.addContent( allowedFromTypes );

        final Element allowedToTypes = new Element( "allowed-to-types" );
        for ( ContentTypeName allowedToType : type.getAllowedToTypes() )
        {
            allowedToTypes.addContent( new Element( "content-type" ).setText( allowedToType.toString() ) );
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
        catch ( JDOMException | IOException e )
        {
            throw new XmlParsingException( "Failed to read XML", e );
        }
    }

    private RelationshipType parse( final Element relationshipTypeEl )
        throws IOException
    {
        final String name = relationshipTypeEl.getChildText( "name" );
        final String displayName = relationshipTypeEl.getChildText( "display-name" );

        final String fromSemantic = relationshipTypeEl.getChildText( "from-semantic" );
        final String toSemantic = relationshipTypeEl.getChildText( "to-semantic" );

        final RelationshipType.Builder relationshipTypeBuilder = RelationshipType.newRelationshipType().
            name( name ).
            displayName( displayName ).
            fromSemantic( fromSemantic ).
            toSemantic( toSemantic );

        final Element allowedFromTypesEl = relationshipTypeEl.getChild( "allowed-from-types" );
        for ( final Element contentTypeEl : getChildren( allowedFromTypesEl, "content-type" ) )
        {
            relationshipTypeBuilder.addAllowedFromType( ContentTypeName.from( contentTypeEl.getText() ) );
        }

        final Element allowedToTypesEl = relationshipTypeEl.getChild( "allowed-to-types" );
        for ( final Element contentTypeEl : getChildren( allowedToTypesEl, "content-type" ) )
        {
            relationshipTypeBuilder.addAllowedToType( ContentTypeName.from( contentTypeEl.getText() ) );
        }

        return relationshipTypeBuilder.build();
    }

    @SuppressWarnings({"unchecked"})
    private List<Element> getChildren( final Element parent, final String name )
    {
        return parent.getChildren( name );
    }
}
