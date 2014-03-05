module api.form.inputtype.content.relationship {

    export interface RelationshipConfig {
        relationshipType: string
    }

    export class Relationship extends api.dom.DivEl implements api.form.inputtype.InputTypeView {

        private config: api.form.inputtype.InputTypeViewConfig<RelationshipConfig>;

        private input: api.form.Input;

        private relationshipTypeName: string;

        private contentComboBox: api.content.ContentComboBox;

        private inputValidityChangedListeners: {(event: api.form.inputtype.InputValidityChangedEvent) : void}[] = [];

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        constructor(config?: api.form.inputtype.InputTypeViewConfig<RelationshipConfig>) {
            super("relationship");
            this.addClass("input-type-view");
            this.config = config;
            this.relationshipTypeName = config.inputConfig.relationshipType;
        }

        availableSizeChanged(newSize: number) {
            console.log("Relationship.availableSizeChanged("+newSize+")" );
        }

        getElement(): api.dom.Element {
            return this;
        }

        isManagingAdd(): boolean {
            return true;
        }

        onOccurrenceAdded(listener: (event: api.form.OccurrenceAddedEvent)=>void) {
            throw new Error("Relationship manages occurrences self");
        }

        onOccurrenceRemoved(listener: (event: api.form.OccurrenceRemovedEvent)=>void) {
            throw new Error("Relationship manages occurrences self");
        }

        unOccurrenceAdded(listener: (event: api.form.OccurrenceAddedEvent)=>void) {
            throw new Error("Relationship manages occurrences self");
        }

        unOccurrenceRemoved(listener: (event: api.form.OccurrenceRemovedEvent)=>void) {
            throw new Error("Relationship manages occurrences self");
        }

        maximumOccurrencesReached(): boolean {
            return this.contentComboBox.maximumOccurrencesReached();
        }

        createAndAddOccurrence() {
            throw new Error("Relationship manages occurrences self");
        }

        layout(input: api.form.Input, properties: api.data.Property[]) {

            this.input = input;
            var relationshipLoader = new RelationshipLoader();
            this.contentComboBox = new api.content.ContentComboBoxBuilder()
                .setName(input.getName())
                .setMaximumOccurrences(input.getOccurrences().getMaximum())
                .setLoader(relationshipLoader)
                .build();

            this.contentComboBox.addOptionSelectedListener((item: api.ui.selector.Option<api.content.ContentSummary>) => {

                this.validate(false);
            });

            this.contentComboBox.addSelectedOptionRemovedListener((removed: api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) => {
                this.validate(false);
            });

            var name = new api.schema.relationshiptype.RelationshipTypeName((this.relationshipTypeName == null) ? "default"
                : this.relationshipTypeName);
            new api.schema.relationshiptype.GetRelationshipTypeByNameRequest(name)
                .sendAndParse().done((relationshipType: api.schema.relationshiptype.RelationshipType) => {
                    this.contentComboBox.setInputIconUrl(relationshipType.getIconUrl());
                    relationshipLoader.setAllowedContentTypes(relationshipType.getAllowedToTypes());
                });

            if (properties != null) {
                properties.forEach((property: api.data.Property) => {
                    new api.content.GetContentByIdRequest(new api.content.ContentId(property.getString()))
                        .setExpand(api.content.ContentResourceRequest.EXPAND_SUMMARY)
                        .sendAndParse().done((contentSummary: api.content.ContentSummary) => {
                            this.contentComboBox.select(contentSummary);
                        });
                });
            }

            this.appendChild(this.contentComboBox);
        }

        getValues(): api.data.Value[] {
            return this.contentComboBox.getStringValues().map((value: string) => {
                return new api.data.Value(value, api.data.ValueTypes.STRING);
            });
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return [];
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

        onValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
            this.inputValidityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
            this.inputValidityChangedListeners.filter((currentListener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValidityChanged(event: api.form.inputtype.InputValidityChangedEvent) {
            this.inputValidityChangedListeners.forEach((listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) => {
                listener(event);
            });
        }

        giveFocus(): boolean {
            if (this.contentComboBox.maximumOccurrencesReached()) {
                return false;
            }
            return this.contentComboBox.giveFocus();
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            return !api.content.ContentId.isValidContentId(value.asString());
        }

        addEditContentRequestListener(listener: (content: api.content.ContentSummary) => void) {
            // Have to use stub here because it doesn't extend BaseIntputTypeView
        }

        removeEditContentRequestListener(listener: (content: api.content.ContentSummary) => void) {
            // Have to use stub here because it doesn't extend BaseIntputTypeView
        }

    }

    api.form.input.InputTypeManager.register("Relationship", Relationship);
}