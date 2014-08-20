module api.content.form.inputtype.relationship {

    export interface RelationshipConfig {
        relationshipType: string
    }

    export class Relationship extends api.form.inputtype.support.BaseInputTypeManagingAdd {

        private config: api.content.form.inputtype.ContentInputTypeViewContext<RelationshipConfig>;

        private input: api.form.Input;

        private relationshipTypeName: api.schema.relationshiptype.RelationshipTypeName;

        private contentComboBox: api.content.ContentComboBox;

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        private layoutInProgress: boolean;

        constructor(config?: api.content.form.inputtype.ContentInputTypeViewContext<RelationshipConfig>) {
            super("relationship");
            this.addClass("input-type-view");
            this.config = config;
            this.relationshipTypeName = config.inputConfig.relationshipType ?
                                        new api.schema.relationshiptype.RelationshipTypeName(config.inputConfig.relationshipType) :
                                        new api.schema.relationshiptype.RelationshipTypeName("default");
        }

        availableSizeChanged() {
            console.log("Relationship.availableSizeChanged(" + this.getEl().getWidth() + "x" + this.getEl().getWidth() + ")");
        }

        newInitialValue(): api.data.Value {
            return null;
        }

        layout(input: api.form.Input, properties: api.data.Property[]) {

            this.layoutInProgress = true;

            this.input = input;

            var relationshipLoader = new RelationshipLoader();

            this.contentComboBox = new api.content.ContentComboBoxBuilder()
                .setName(input.getName())
                .setMaximumOccurrences(input.getOccurrences().getMaximum())
                .setLoader(relationshipLoader)
                .build();

            this.contentComboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.ContentSummary>) => {

                if (!this.layoutInProgress) {
                    var value = new api.data.Value(event.getOption().displayValue.getContentId(), api.data.ValueTypes.CONTENT_ID);
                    this.notifyValueAdded(value);
                }
                this.validate(false);
            });

            this.contentComboBox.onSelectedOptionRemoved((removed: api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) => {

                this.notifyValueRemoved(removed.getIndex());
                this.validate(false);
            });


            new api.schema.relationshiptype.GetRelationshipTypeByNameRequest(this.relationshipTypeName).
                sendAndParse().
                done((relationshipType: api.schema.relationshiptype.RelationshipType) => {

                    this.contentComboBox.setInputIconUrl(relationshipType.getIconUrl());
                    relationshipLoader.setAllowedContentTypes(relationshipType.getAllowedToTypes());

                    relationshipLoader.load();
                    this.appendChild(this.contentComboBox);

                    this.doLoadContent(properties).
                        then((contents: api.content.ContentSummary[]) => {

                            contents.forEach((content: api.content.ContentSummary) => {
                                this.contentComboBox.select(content);
                            });

                        }).catch((reason: any) => {

                            api.DefaultErrorHandler.handle(reason);

                        }).finally(()=> {

                            this.layoutInProgress = false;

                        }).done();
                });
        }

        private doLoadContent(properties: api.data.Property[]): Q.Promise<api.content.ContentSummary[]> {

            if (!properties) {
                return Q<api.content.ContentSummary[]>([]);
            }
            else {
                var contentIds = properties.map((property: api.data.Property) => {
                    return new api.content.ContentId(property.getString());
                });
                return new api.content.GetContentSummaryByIds(contentIds).get().
                    then((result: api.content.ContentSummary[]) => {
                        return result;
                    });
            }
        }

        getValues(): api.data.Value[] {
            var values: api.data.Value[] = [];
            this.contentComboBox.getSelectedValues().forEach((content: api.content.ContentSummary) => {
                var value = new api.data.Value(content.getContentId(), api.data.ValueTypes.CONTENT_ID);
                values.push(value);
            });
            return values;
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

    api.form.inputtype.InputTypeManager.register(new api.Class("Relationship", Relationship));
}