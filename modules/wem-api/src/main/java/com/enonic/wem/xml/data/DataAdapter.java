package com.enonic.wem.xml.data;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DataAdapter
    extends XmlAdapter<Element, DataXml>
{
    private DocumentBuilder documentBuilder;

    private JAXBContext jaxbContext;

    public DataAdapter()
    {
    }

    public DataAdapter( JAXBContext jaxbContext )
    {
        this.jaxbContext = jaxbContext;
    }

    private DocumentBuilder getDocumentBuilder()
        throws Exception
    {
        // Lazy load the DocumentBuilder as it is not used for unmarshalling.
        if ( null == documentBuilder )
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            documentBuilder = dbf.newDocumentBuilder();
        }
        return documentBuilder;
    }

    private JAXBContext getJAXBContext( Class<?> type )
        throws Exception
    {
        if ( null == jaxbContext )
        {
            return JAXBContext.newInstance( type );
        }
        return jaxbContext;
    }

    @Override
    public Element marshal( DataXml dataXml )
        throws Exception
    {
        if ( null == dataXml )
        {
            return null;
        }

        final Object value;
        final Class<?> type;
        if ( dataXml.isDataSet() )
        {
            value = dataXml.getItems();
            type = value.getClass();
        }
        else
        {
            value = dataXml.getValue();
            type = String.class;
        }
        final QName rootElement = new QName( dataXml.getName() );
        final JAXBElement jaxbElement = new JAXBElement( rootElement, type, value );

        final Document document = getDocumentBuilder().newDocument();
        final Marshaller marshaller = getJAXBContext( type ).createMarshaller();
        marshaller.marshal( jaxbElement, document );
        final Element element = document.getDocumentElement();

        element.setAttribute( "type", dataXml.getType() );
        return element;
    }

    @Override
    public DataXml unmarshal( Element element )
        throws Exception
    {
        if ( null == element )
        {
            return null;
        }
        final DataXml dataXml = new DataXml();
        dataXml.setName( element.getLocalName() );
        dataXml.setType( element.getAttribute( "type" ) );
        dataXml.setValue( element.getTextContent() );
        return dataXml;
    }

}