package com.enonic.wem.api.xml.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "dataPropertyType")
@XmlEnum
public enum XmlDataPropertyType
{
    @XmlEnumValue("Boolean")
    BOOLEAN( "Boolean" ),
    @XmlEnumValue("String")
    STRING( "String" ),
    @XmlEnumValue("Data")
    DATA( "Data" ),
    @XmlEnumValue("HtmlPart")
    HTML_PART( "HtmlPart" ),
    @XmlEnumValue("Double")
    DOUBLE( "Double" ),
    @XmlEnumValue("Long")
    LONG( "Long" ),
    @XmlEnumValue("Xml")
    XML( "Xml" ),
    @XmlEnumValue("LocalDate")
    LOCAL_DATE( "LocalDate" ),
    @XmlEnumValue("LocalDateTime")
    LOCAL_DATE_TIME( "LocalDateTime" ),
    @XmlEnumValue("LocalTime")
    LOCAL_TIME( "LocalTime" ),
    @XmlEnumValue("DateTime")
    DATE_TIME( "DateTime" ),
    @XmlEnumValue("ContentId")
    CONTENT_ID( "ContentId" ),
    @XmlEnumValue("GeoPoint")
    GEO_POINT( "GeoPoint" );

    private final String value;

    private XmlDataPropertyType( String v )
    {
        value = v;
    }

    public String value()
    {
        return value;
    }
}
