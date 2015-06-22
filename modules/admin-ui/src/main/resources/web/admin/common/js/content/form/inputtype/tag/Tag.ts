module api.content.form.inputtype.tag {

    import PropertyPath = api.data.PropertyPath;
    import PropertyPathElement = api.data.PropertyPathElement;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class Tag extends api.form.inputtype.support.BaseInputTypeManagingAdd<string> {

        private tags: api.ui.tags.Tags;

        private tagSuggester: ContentTagSuggester;

        constructor(context: api.content.form.inputtype.ContentInputTypeViewContext<any>) {
            super("tag");

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
            super.layout(input, propertyArray);

            this.tags.clearTags();
            propertyArray.forEach((property) => {
                if (property.hasNonNullValue()) {
                    this.tags.addTag(property.getString());
                }
            });

            this.tags.onTagAdded((event) => {
                var value = new Value(event.getValue(), ValueTypes.STRING);
                if (this.tags.countTags() == 1) {
                    this.getPropertyArray().set(0, value);
                }
                else {
                    this.getPropertyArray().add(value);
                }
                this.validate(false);
            });

            this.tags.onTagRemoved((event) => {
                this.getPropertyArray().remove(event.getIndex());
                this.validate(false);
            });

            this.setLayoutInProgress(false);

            return wemQ<void>(null);
        }

        protected getNumberOfValids(): number {
            return this.tags.countTags();
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