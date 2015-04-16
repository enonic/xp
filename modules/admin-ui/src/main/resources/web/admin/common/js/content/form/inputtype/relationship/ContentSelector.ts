module api.content.form.inputtype.relationship {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import GetRelationshipTypeByNameRequest = api.schema.relationshiptype.GetRelationshipTypeByNameRequest;

    export interface ContentSelectorConfig {
        relationshipType: string
        allowedContentTypes: string[];
    }

    export class ContentSelector extends api.form.inputtype.support.BaseInputTypeManagingAdd<api.content.ContentId> {

        private config: api.content.form.inputtype.ContentInputTypeViewContext<ContentSelectorConfig>;

        private input: api.form.Input;

        private propertyArray: PropertyArray;

        private relationshipTypeName: api.schema.relationshiptype.RelationshipTypeName;

        private contentComboBox: api.content.ContentComboBox;

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        constructor(config?: api.content.form.inputtype.ContentInputTypeViewContext<ContentSelectorConfig>) {
            super("relationship");
            this.addClass("input-type-view");
            this.config = config;
            this.relationshipTypeName = config.inputConfig.relationshipType ?
                                        new api.schema.relationshiptype.RelationshipTypeName(config.inputConfig.relationshipType) :
                                        api.schema.relationshiptype.RelationshipTypeName.REFERENCE;
        }

        availableSizeChanged() {
            console.log("Relationship.availableSizeChanged(" + this.getEl().getWidth() + "x" + this.getEl().getWidth() + ")");
        }

        getValueType(): ValueType {
            return ValueTypes.REFERENCE;
        }

        newInitialValue(): Value {
            return null;
        }

        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {

            this.input = input;
            this.propertyArray = propertyArray;

            var relationshipLoader = new ContentSelectorLoader();

            this.contentComboBox = api.content.ContentComboBox.create()
                .setName(input.getName())
                .setMaximumOccurrences(input.getOccurrences().getMaximum())
                .setLoader(relationshipLoader)
                .build();

            return new GetRelationshipTypeByNameRequest(this.relationshipTypeName).
                sendAndParse().
                then((relationshipType: api.schema.relationshiptype.RelationshipType) => {

                    this.contentComboBox.setInputIconUrl(relationshipType.getIconUrl());
                    var inputAllowedContentTypes = this.config.inputConfig.allowedContentTypes || [];
                    var relationshipAllowedContentTypes = relationshipType.getAllowedToTypes() || [];
                    var allowedContentTypes = inputAllowedContentTypes.length ? inputAllowedContentTypes : relationshipAllowedContentTypes;
                    relationshipLoader.setAllowedContentTypes(allowedContentTypes);

                    this.appendChild(this.contentComboBox);

                    return this.doLoadContent(propertyArray).
                        then((contents: api.content.ContentSummary[]) => {

                            contents.forEach((content: api.content.ContentSummary) => {
                                this.contentComboBox.select(content);
                            });

                            this.contentComboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.ContentSummary>) => {

                                var reference = api.util.Reference.from(event.getOption().displayValue.getContentId());

                                var value = new Value(reference, ValueTypes.REFERENCE);
                                if (this.contentComboBox.countSelected() == 1) { // overwrite initial value
                                    this.propertyArray.set(0, value);
                                }
                                else {
                                    this.propertyArray.add(value);
                                }

                                this.validate(false);
                            });

                            this.contentComboBox.onOptionDeselected((removed: api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) => {

                                this.propertyArray.remove(removed.getIndex());
                                this.validate(false);
                            });

                        });
                });
        }

        private doLoadContent(propertyArray: PropertyArray): wemQ.Promise<api.content.ContentSummary[]> {

            var contentIds: ContentId[] = [];
            propertyArray.forEach((property: Property) => {
                if (property.hasNonNullValue()) {
                    var referenceValue = property.getReference();
                    if (referenceValue instanceof api.util.Reference) {
                        contentIds.push(ContentId.fromReference(referenceValue));
                    }
                }
            });
            return new api.content.GetContentSummaryByIds(contentIds).get().
                then((result: api.content.ContentSummary[]) => {
                    return result;
                });

        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            var recording = new api.form.inputtype.InputValidationRecording();

            var numberOfValids = this.contentComboBox.countSelected();
            if (numberOfValids < this.input.getOccurrences().getMinimum()) {
                recording.setBreaksMinimumOccurrences(true);
            }
            if (this.input.getOccurrences().maximumBreached(numberOfValids)) {
                recording.setBreaksMaximumOccurrences(true);
            }

            if (!silent) {
                if (recording.validityChanged(this.previousValidationRecording)) {
                    this.notifyValidityChanged(new api.form.inputtype.InputValidityChangedEvent(recording, this.input.getName()));
                }
            }

            this.previousValidationRecording = recording;
            return recording;
        }

        giveFocus(): boolean {
            if (this.contentComboBox.maximumOccurrencesReached()) {
                return false;
            }
            return this.contentComboBox.giveFocus();
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.contentComboBox.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.contentComboBox.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.contentComboBox.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.contentComboBox.unBlur(listener);
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("ContentSelector", ContentSelector));
}