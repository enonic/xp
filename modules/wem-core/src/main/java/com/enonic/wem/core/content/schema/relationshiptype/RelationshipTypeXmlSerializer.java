package com.enonic.wem.core.content.schema.relationshiptype;


import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.relationshiptype.RelationshipType;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.support.serializer.XmlParsingException;
import com.enonic.wem.core.util.JdomHelper;

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
        typeEl.addContent( new Element( "module" ).setText( type.getModuleName().toString() ) );

        typeEl.addContent( new Element( "from-semantic" ).setText( type.getFromSemantic() ) );
        typeEl.addContent( new Element( "to-semantic" ).setText( type.getToSemantic() ) );

        final Element allowedFromTypes = new Element( "allowed-from-types" );
        for ( QualifiedContentTypeName allowedFromType : type.getAllowedFromTypes() )
        {
            allowedFromTypes.addContent( new Element( "content-type" ).setText( allowedFromType.toString() ) );
        }
        typeEl.addContent( allowedFromTypes );

        final Element allowedToTypes = new Element( "allowed-to-types" );
        for ( QualifiedContentTypeName allowedToType : type.getAllowedToTypes() )
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
        final ModuleName module = ModuleName.from( relationshipTypeEl.getChildText( "module" ) );
        final String name = relationshipTypeEl.getChildText( "name" );
        final String displayName = relationshipTypeEl.getChildText( "display-name" );

        final String fromSemantic = relationshipTypeEl.getChildText( "from-semantic" );
        final String toSemantic = relationshipTypeEl.getChildText( "to-semantic" );

        final RelationshipType.Builder relationshipTypeBuilder = RelationshipType.newRelationshipType().
            name( name ).
            module( module ).
            displayName( displayName ).
            fromSemantic( fromSemantic ).
            toSemantic( toSemantic );

        final Element allowedFromTypesEl = relationshipTypeEl.getChild( "allowed-from-types" );
        for ( final Element contentTypeEl : getChildren( allowedFromTypesEl, "content-type" ) )
        {
            relationshipTypeBuilder.addAllowedFromType( new QualifiedContentTypeName( contentTypeEl.getText() ) );
        }

        final Element allowedToTypesEl = relationshipTypeEl.getChild( "allowed-to-types" );
        for ( final Element contentTypeEl : getChildren( allowedToTypesEl, "content-type" ) )
        {
            relationshipTypeBuilder.addAllowedToType( new QualifiedContentTypeName( contentTypeEl.getText() ) );
        }

        return relationshipTypeBuilder.build();
    }

    @SuppressWarnings({"unchecked"})
    private List<Element> getChildren( final Element parent, final String name )
    {
        return parent.getChildren( name );
    }
}
