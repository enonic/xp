module api.content.form.inputtype.tag {

    import PropertyPath = api.data.PropertyPath;
    import PropertyPathElement = api.data.PropertyPathElement;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class Tag extends api.form.inputtype.support.BaseInputTypeManagingAdd<string> {

        private input: api.form.Input;

        private propertyArray: PropertyArray;

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

        private resolveDataPath(context: api.content.form.inputtype.ContentInputTypeViewContext<any>): PropertyPath {
            if (context.parentDataPath) {
                return PropertyPath.fromParent(context.parentDataPath, PropertyPathElement.fromString(context.input.getName()));
            }
            else {
                return new PropertyPath([PropertyPathElement.fromString(context.input.getName())], false);
            }
        }

        availableSizeChanged() {

        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return null;
        }

        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {

            this.propertyArray = propertyArray;
            this.tags.clearTags();
            propertyArray.forEach((property) => {
                if (property.hasNonNullValue()) {
                    this.tags.addTag(property.getString());
                }
            });

            this.tags.onTagAdded((event) => {
                var value = new Value(event.getValue(), ValueTypes.STRING);
                if (this.tags.countTags() == 1) {
                    this.propertyArray.set(0, value);
                }
                else {
                    this.propertyArray.add(value);
                }
                this.validate(false);
            });

            this.tags.onTagRemoved((event) => {
                this.propertyArray.remove(event.getIndex());
                this.validate(false);
            });

            return wemQ<void>(null);
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {
            debugger;
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