module api.form.inputtype.content.relationship {

    import InputTypeEvent = api.form.inputtype.support.InputTypeEvent;
    import InputTypeEvents = api.form.inputtype.support.InputTypeEvents;
    import ValidityChangedEvent = api.form.inputtype.support.ValidityChangedEvent;

    export interface RelationshipConfig {
        relationshipType: string
    }

    export class Relationship extends api.dom.DivEl implements api.form.inputtype.InputTypeView {

        private input: api.form.Input;

        private relationshipTypeName: string;

        private contentComboBox: api.content.ContentComboBox;

        private listeners: {[eventName:string]:{(event: InputTypeEvent):void}[]} = {};

        private previousValidationRecording: api.form.ValidationRecorder;

        constructor(config?: api.form.inputtype.InputTypeViewConfig<RelationshipConfig>) {
            super("relationship");
            this.addClass("input-type-view");

            this.listeners[InputTypeEvents.ValidityChanged] = [];
            this.relationshipTypeName = config.inputConfig.relationshipType;
        }

        getHTMLElement(): HTMLElement {
            return super.getHTMLElement();
        }

        isManagingAdd(): boolean {
            return true;
        }

        addFormItemOccurrencesListener(listener: api.form.FormItemOccurrencesListener) {
            throw new Error("Relationship manages occurrences self");
        }

        removeFormItemOccurrencesListener(listener: api.form.FormItemOccurrencesListener) {
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

            this.contentComboBox.addOptionSelectedListener((item: api.ui.combobox.Option<api.content.ContentSummary>) => {

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

        validate(silent: boolean = true) {

            var recording: api.form.ValidationRecorder = new api.form.ValidationRecorder();

            var numberOfValids = this.contentComboBox.countSelected();
            if (numberOfValids < this.input.getOccurrences().getMinimum()) {
                recording.breaksMinimumOccurrences(this.input.getPath());
            }
            if (numberOfValids > this.input.getOccurrences().getMaximum()) {
                recording.breaksMaximumOccurrences(this.input.getPath());
            }

            if (!silent) {
                if (recording.validityChanged(this.previousValidationRecording)) {
                    this.notifyValidityChanged(new support.ValidityChangedEvent(recording, this.input.getPath()));
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

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            return !api.content.ContentId.isValidContentId(value.asString());
        }

        addEditContentRequestListener(listener: (content: api.content.ContentSummary) => void) {
            // Have to use stub here because it doesn't extend BaseIntputTypeView
        }

        removeEditContentRequestListener(listener: (content: api.content.ContentSummary) => void) {
            // Have to use stub here because it doesn't extend BaseIntputTypeView
        }

        private addListener(eventName: InputTypeEvents, listener: (event: InputTypeEvent)=>void) {
            this.listeners[eventName].push(listener);
        }

        onValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.addListener(InputTypeEvents.ValidityChanged, listener);
        }

        private removeListener(eventName: InputTypeEvents, listener: (event: InputTypeEvent)=>void) {
            this.listeners[eventName].filter((currentListener: (event: InputTypeEvent)=>void) => {
                return listener == currentListener;
            });
        }

        unValidityChanged(listener: (event: ValidityChangedEvent)=>void) {
            this.removeListener(InputTypeEvents.ValidityChanged, listener);
        }

        private notifyListeners(eventName: InputTypeEvents, event: InputTypeEvent) {
            this.listeners[eventName].forEach((listener: (event: InputTypeEvent)=>void) => {
                listener(event);
            });
        }

        private notifyValidityChanged(event: ValidityChangedEvent) {
            this.notifyListeners(InputTypeEvents.ValidityChanged, event);
        }
    }

    api.form.input.InputTypeManager.register("Relationship", Relationship);
}