module api.content.form.inputtype.tag {

    import DataPath = api.data.DataPath;

    export class Tag extends api.form.inputtype.support.BaseInputTypeManagingAdd<string> {

        private input: api.form.Input;

        private tags: api.ui.tags.Tags;

        private tagSuggester: ContentTagSuggester;

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

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

        getValueType(): api.data.type.ValueType {
            return api.data.type.ValueTypes.STRING;
        }

        newInitialValue(): string {
            return null;
        }

        layout(input: api.form.Input, properties: api.data.Property[]) {

            this.tags.clearTags();
            properties.forEach((property) => {
                if (property.hasNonNullValue()) {
                    this.tags.addTag(property.getString());
                }
            });

            this.tags.onTagAdded((event) => {
                this.notifyValueAdded(new api.data.Value(event.getValue(), api.data.type.ValueTypes.STRING));
            });

            this.tags.onTagRemoved((event) => {
                this.notifyValueRemoved(event.getIndex());
            });
        }

        getValues(): api.data.Value[] {
            var values: api.data.Value[] = [];
            this.tags.getTags().forEach((tag) => {
                values.push(new api.data.Value(tag, api.data.type.ValueTypes.STRING));
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

        giveFocus(): boolean {
            return this.tags.giveFocus();
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.tags.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.tags.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.tags.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.tags.unBlur(listener);
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("Tag", Tag));
}