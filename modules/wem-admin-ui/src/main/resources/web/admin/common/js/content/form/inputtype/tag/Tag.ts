module api.content.form.inputtype.tag {

    import DataPath = api.data.DataPath;

    export class Tag extends api.dom.DivEl implements api.form.inputtype.InputTypeView {

        private input: api.form.Input;

        private tags: api.ui.tags.Tags;

        private tagSuggester: ContentTagSuggester;

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        private valueAddedListeners: {(event: api.form.inputtype.ValueAddedEvent) : void}[] = [];

        private valueRemovedListeners: {(event: api.form.inputtype.ValueRemovedEvent) : void}[] = [];

        private inputValidityChangedListeners: {(event: api.form.inputtype.InputValidityChangedEvent) : void}[] = [];

        constructor(context: api.content.form.inputtype.ContentInputTypeViewContext<any>) {
            super("tag");
            this.input = context.input;
            this.addClass("input-type-view");
            var dataPath = this.resolveDataPath(context);
            this.tagSuggester = new ContentTagSuggesterBuilder().
                setDataPath(dataPath).
                build();

            var tagsBuilder = new api.ui.tags.TagsBuilder().
                setTagSuggester(this.tagSuggester).
                setMaxTags(context.input.getOccurrences().getMaximum());
            this.tags = tagsBuilder.build();
            this.appendChild(this.tags);
        }

        private resolveDataPath(config: api.content.form.inputtype.ContentInputTypeViewContext<any>): DataPath {
            if (config.parentDataPath) {
                return api.data.DataPath.fromParent(config.parentDataPath, api.data.DataPathElement.fromString(config.input.getName()));
            }
            else {
                return new api.data.DataPath([api.data.DataPathElement.fromString(config.input.getName())], false);
            }
        }

        availableSizeChanged() {

        }

        getElement(): api.dom.Element {
            return this;
        }

        isManagingAdd(): boolean {
            return true;
        }

        newInitialValue(): api.data.Value {
            return null;
        }

        layout(input: api.form.Input, properties: api.data.Property[]) {

            this.tags.clearTags();
            properties.forEach((property) => {
                this.tags.addTag(property.getString());
            });

            this.tags.onTagAdded((event) => {
                this.notifyValueAdded(new api.data.Value(event.getValue(), api.data.ValueTypes.STRING));
            });

            this.tags.onTagRemoved((event) => {
                this.notifyValueRemoved(event.getIndex());
            });
        }

        getValues(): api.data.Value[] {
            var values: api.data.Value[] = [];
            this.tags.getTags().forEach((tag) => {
                values.push(new api.data.Value(tag, api.data.ValueTypes.STRING));
            });
            return values;
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return [];
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            var recording = new api.form.inputtype.InputValidationRecording();

            var numberOfValids = this.tags.countTags();
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

        onValueAdded(listener: (event: api.form.inputtype.ValueAddedEvent) => void) {
            this.valueAddedListeners.push(listener);
        }

        unValueAdded(listener: (event: api.form.inputtype.ValueAddedEvent) => void) {
            this.valueAddedListeners.filter((currentListener: (event: api.form.inputtype.ValueAddedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValueAdded(value: api.data.Value) {
            var event = new api.form.inputtype.ValueAddedEvent(value);
            this.valueAddedListeners.forEach((listener: (event: api.form.inputtype.ValueAddedEvent)=>void) => {
                listener(event);
            });
        }

        onValueChanged(listener: (event: api.form.inputtype.ValueChangedEvent) => void) {
            // A tag value never changes
        }

        unValueChanged(listener: (event: api.form.inputtype.ValueChangedEvent) => void) {
            // A tag value never changes
        }

        onValueRemoved(listener: (event: api.form.inputtype.ValueRemovedEvent) => void) {
            this.valueRemovedListeners.push(listener);
        }

        unValueRemoved(listener: (event: api.form.inputtype.ValueRemovedEvent) => void) {
            this.valueRemovedListeners.filter((currentListener: (event: api.form.inputtype.ValueRemovedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValueRemoved(index: number) {
            var event = new api.form.inputtype.ValueRemovedEvent(index);
            this.valueRemovedListeners.forEach((listener: (event: api.form.inputtype.ValueRemovedEvent)=>void) => {
                listener(event);
            });
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
            return false;
        }

        onEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            // Have to use stub here because it doesn't extend BaseInputTypeView
        }

        unEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            // Have to use stub here because it doesn't extend BaseInputTypeView
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("Tag", Tag));
}