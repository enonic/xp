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

        private relationshipTypeName: api.schema.relationshiptype.RelationshipTypeName;

        private contentComboBox: api.content.ContentComboBox;

        private draggingIndex: number;

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

            super.layout(input, propertyArray);

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
                                    this.getPropertyArray().set(0, value);
                                }
                                else if (!this.getPropertyArray().containsValue(value)) {
                                    this.getPropertyArray().add(value);
                                }

                                this.refreshSortable();
                                this.updateSelectedOptionStyle();
                                this.validate(false);
                            });

                            this.contentComboBox.onOptionDeselected((removed: api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) => {

                                this.getPropertyArray().remove(removed.getIndex());
                                this.updateSelectedOptionStyle();
                                this.validate(false);
                            });

                            this.setupSortable();

                            this.setLayoutInProgress(false);
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

        private setupSortable() {
            wemjq(this.getHTMLElement()).find(".selected-options").sortable({
                axis: "y",
                containment: 'parent',
                handle: '.drag-control',
                tolerance: 'pointer',
                start: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDStart(event, ui),
                update: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDUpdate(event, ui)
            });

            this.updateSelectedOptionStyle();
        }

        private handleDnDStart(event: Event, ui: JQueryUI.SortableUIParams): void {

            var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
            this.draggingIndex = draggedElement.getSiblingIndex();

            ui.placeholder.html("Drop form item set here");
        }

        private handleDnDUpdate(event: Event, ui: JQueryUI.SortableUIParams) {

            if (this.draggingIndex >= 0) {
                var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
                var draggedToIndex = draggedElement.getSiblingIndex();
                this.getPropertyArray().move(this.draggingIndex, draggedToIndex);
            }

            this.draggingIndex = -1;
        }

        private updateSelectedOptionStyle() {
            if (this.getPropertyArray().getSize() > 1) {
                this.addClass("multiple-occurrence").removeClass("single-occurrence");
            }
            else {
                this.addClass("single-occurrence").removeClass("multiple-occurrence");
            }
        }

        private refreshSortable() {
            wemjq(this.getHTMLElement()).find(".selected-options").sortable("refresh");
        }

        protected getNumberOfValids(): number {
            return this.contentComboBox.countSelected();
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