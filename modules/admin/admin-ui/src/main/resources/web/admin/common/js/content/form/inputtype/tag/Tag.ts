module api.content.form.inputtype.tag {

    import PropertyPath = api.data.PropertyPath;
    import PropertyPathElement = api.data.PropertyPathElement;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class Tag extends api.form.inputtype.support.BaseInputTypeManagingAdd<string> {

        private context: api.content.form.inputtype.ContentInputTypeViewContext;

        private tags: api.ui.tags.Tags;

        private tagSuggester: ContentTagSuggester;

        constructor(context: api.content.form.inputtype.ContentInputTypeViewContext) {
            super("tag");
            this.addClass("input-type-view");

            this.context = context;

            this.tagSuggester = new ContentTagSuggesterBuilder().
                setDataPath(this.resolveDataPath(context)).
                build();
        }

        private resolveDataPath(context: api.content.form.inputtype.ContentInputTypeViewContext): PropertyPath {
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
            if (!ValueTypes.STRING.equals(propertyArray.getType())) {
                propertyArray.convertValues(ValueTypes.STRING);
            }
            super.layout(input, propertyArray);

            var tagsBuilder = new api.ui.tags.TagsBuilder().
                setTagSuggester(this.tagSuggester).
                setMaxTags(this.context.input.getOccurrences().getMaximum());

            propertyArray.forEach((property) => {
                var value = property.getString();
                if (value) {
                    tagsBuilder.addTag(value);
                }
            });

            this.tags = tagsBuilder.build();
            this.appendChild(this.tags);

            this.tags.onTagAdded((event: api.ui.tags.TagAddedEvent) => {
                this.ignorePropertyChange = true;
                var value = new Value(event.getValue(), ValueTypes.STRING);
                if (this.tags.countTags() == 1) {
                    this.getPropertyArray().set(0, value);
                }
                else {
                    this.getPropertyArray().add(value);
                }
                this.validate(false);
                this.ignorePropertyChange = false;
            });

            this.tags.onTagRemoved((event: api.ui.tags.TagRemovedEvent) => {
                this.ignorePropertyChange = true;
                this.getPropertyArray().remove(event.getIndex());
                this.validate(false);
                this.ignorePropertyChange = false;
            });

            this.setLayoutInProgress(false);

            return wemQ<void>(null);
        }


        update(propertyArray: api.data.PropertyArray, unchangedOnly?: boolean): Q.Promise<void> {
            var superPromise = super.update(propertyArray, unchangedOnly);

            if (!unchangedOnly || !this.tags.isDirty()) {
                superPromise.then(() => {
                    this.tags.setValue(this.getValueFromPropertyArray(propertyArray));
                });
            } else {
                return superPromise;
            }
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