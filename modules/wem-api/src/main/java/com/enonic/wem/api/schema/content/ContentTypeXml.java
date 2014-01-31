package com.enonic.wem.api.schema.content;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormXml;
import com.enonic.wem.xml.XmlObject;

@XmlRootElement(name = "type")
public class ContentTypeXml
    implements XmlObject<ContentType, ContentType.Builder>
{
    @XmlElement(name = "display-name", required = false)
    private String displayName;

    @XmlElement(name = "content-display-name-script", required = false)
    private String contentDisplayNameScript;

    @XmlElement(name = "super-type", required = false)
    private String superType;

    @XmlElement(name = "is-abstract", required = false)
    private boolean isAbstract;

    @XmlElement(name = "is-final", required = false)
    private boolean isFinal;

    @XmlElement(name = "is-built-in", required = false)
    private boolean isBuiltIn;

    @XmlElement(name = "allow-child-content", required = false)
    private String allowChildContent;

    @XmlElement(name = "form", required = false)
    private FormXml formXml = new FormXml();


    @Override
    public void from( final ContentType type )
    {
        this.displayName = type.getDisplayName();
        this.contentDisplayNameScript = type.getContentDisplayNameScript();
        this.superType = type.getSuperType() != null ? type.getSuperType().toString() : null;
        this.isAbstract = type.isAbstract();
        this.isFinal = type.isFinal();
        this.isBuiltIn = type.isBuiltIn();
        this.allowChildContent = Boolean.toString( type.allowChildContent() );

        formXml.from( type.form() );
    }

    @Override
    public void to( final ContentType.Builder builder )
    {
        final String superTypeString = StringUtils.trimToNull( superType );

        final Form.Builder form = Form.newForm();
        formXml.to( form );

        builder.
            displayName( displayName ).
            contentDisplayNameScript( contentDisplayNameScript ).
            superType( ContentTypeName.from( superTypeString ) ).
            setAbstract( isAbstract ).
            setFinal( isFinal ).
            builtIn( isBuiltIn ).
            allowChildContent( StringUtils.isBlank( allowChildContent ) || Boolean.parseBoolean( allowChildContent ) ).
            form( form.build() );
    }
}
