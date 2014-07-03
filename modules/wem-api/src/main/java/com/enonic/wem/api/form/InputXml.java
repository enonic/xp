package com.enonic.wem.api.form;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.enonic.wem.api.form.inputtype.ConfigXml;
import com.enonic.wem.api.form.inputtype.ComboBoxConfig;
import com.enonic.wem.api.form.inputtype.ComboBoxConfigXml;
import com.enonic.wem.api.form.inputtype.ImageSelectorConfig;
import com.enonic.wem.api.form.inputtype.ImageSelectorConfigXml;
import com.enonic.wem.api.form.inputtype.InputType;
import com.enonic.wem.api.form.inputtype.InputTypeConfig;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.form.inputtype.RelationshipConfig;
import com.enonic.wem.api.form.inputtype.RelationshipConfigXml;
import com.enonic.wem.api.form.inputtype.SingleSelectorConfig;
import com.enonic.wem.api.form.inputtype.SingleSelectorConfigXml;
import com.enonic.wem.api.xml.XmlObject;
import com.enonic.wem.api.xml.XmlSerializers;

@Deprecated
@XmlRootElement(name = "input")
public final class InputXml
    implements XmlObject<Input, Input.Builder>, FormItemXml
{
    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "type", required = true)
    private String type;

    @XmlElement(name = "label", required = false)
    private String label;

    @XmlElement(name = "immutable", required = false)
    private Boolean immutable;

    @XmlElement(name = "indexed", required = false)
    private Boolean indexed;

    @XmlElement(name = "custom-text", required = false)
    private String customText;

    @XmlElement(name = "occurrences")
    private OccurrencesXml occurrences = new OccurrencesXml();

    @XmlJavaTypeAdapter(ConfigXmlAdapter.class)
    private ConfigXml config;

    @XmlElement(name = "help-text", required = false)
    private String helpText;

    @XmlElement(name = "validation-regexp", required = false)
    private String validationRegexp;

    @Override
    public void from( final Input input )
    {
        this.name = input.getName();
        this.type = input.getInputType().getName();
        this.label = input.getLabel();
        this.immutable = input.isImmutable();
        this.indexed = input.isIndexed();
        this.customText = input.getCustomText();
        this.occurrences.from( input.getOccurrences() );
        this.helpText = input.getHelpText();
        this.validationRegexp = input.getValidationRegexp() != null ? input.getValidationRegexp().toString() : null;

        final InputType inputType = input.getInputType();
        final InputTypeConfig inputTypeConfig = input.getInputTypeConfig();

        if ( inputType.requiresConfig() && inputTypeConfig != null )
        {
            switch ( this.type )
            {
                case "ComboBox":
                    final ComboBoxConfigXml comboBoxConfigXml = new ComboBoxConfigXml();
                    comboBoxConfigXml.from( ComboBoxConfig.class.cast( inputTypeConfig ) );
                    this.config = comboBoxConfigXml;
                    break;
                case "SingleSelector":
                    final SingleSelectorConfigXml singleSelectorConfigXml = new SingleSelectorConfigXml();
                    singleSelectorConfigXml.from( SingleSelectorConfig.class.cast( inputTypeConfig ) );
                    this.config = singleSelectorConfigXml;
                    break;
                case "Relationship":
                    final RelationshipConfigXml relationshipConfigXml = new RelationshipConfigXml();
                    relationshipConfigXml.from( RelationshipConfig.class.cast( inputTypeConfig ) );
                    this.config = relationshipConfigXml;
                    break;
                case "ImageSelector":
                    final ImageSelectorConfigXml imageSelectorConfigXml = new ImageSelectorConfigXml();
                    imageSelectorConfigXml.from( ImageSelectorConfig.class.cast( inputTypeConfig ) );
                    this.config = imageSelectorConfigXml;
                    break;
            }
        }
    }

    @Override
    public void to( final Input.Builder output )
    {
        final Occurrences.Builder occurrences = Occurrences.newOccurrences();
        this.occurrences.to( occurrences );

        switch ( this.type )
        {
            case "ComboBox":
                final ComboBoxConfigXml comboBoxConfigXml =
                    XmlSerializers.create( ComboBoxConfigXml.class ).parse( this.config.getElement() );
                final ComboBoxConfig.Builder comboBoxConfigBuilder = ComboBoxConfig.newComboBoxConfig();
                comboBoxConfigXml.to( comboBoxConfigBuilder );
                output.inputTypeConfig( comboBoxConfigBuilder.build() );
                break;
            case "SingleSelector":
                final SingleSelectorConfigXml singleSelectorConfigXml =
                    XmlSerializers.create( SingleSelectorConfigXml.class ).parse( this.config.getElement() );
                final SingleSelectorConfig.Builder singleSelectorConfigBuilder = SingleSelectorConfig.newSingleSelectorConfig();
                singleSelectorConfigXml.to( singleSelectorConfigBuilder );
                output.inputTypeConfig( singleSelectorConfigBuilder.build() );
                break;
            case "Relationship":
                final RelationshipConfigXml relationshipConfigXml =
                    XmlSerializers.create( RelationshipConfigXml.class ).parse( this.config.getElement() );
                final RelationshipConfig.Builder relationshipConfigBuilder = RelationshipConfig.newRelationshipConfig();
                relationshipConfigXml.to( relationshipConfigBuilder );
                output.inputTypeConfig( relationshipConfigBuilder.build() );
                break;
            case "ImageSelector":
                final ImageSelectorConfigXml imageSelectorConfigXml =
                    XmlSerializers.create( ImageSelectorConfigXml.class ).parse( this.config.getElement() );
                final ImageSelectorConfig.Builder imageSelectorConfigBuilder = ImageSelectorConfig.newImageSelectorConfig();
                imageSelectorConfigXml.to( imageSelectorConfigBuilder );
                output.inputTypeConfig( imageSelectorConfigBuilder.build() );
                break;
        }

        output.name( this.name ).
            inputType( InputTypes.parse( this.type ) ).
            label( this.label ).
            immutable( this.immutable ).
            indexed( this.indexed ).
            customText( this.customText ).
            occurrences( occurrences.build() ).
            helpText( this.helpText ).
            validationRegexp( this.validationRegexp );
    }
}
