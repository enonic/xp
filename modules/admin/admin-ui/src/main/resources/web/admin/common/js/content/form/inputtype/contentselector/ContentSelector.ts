module api.content.form.inputtype.contentselector {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import GetRelationshipTypeByNameRequest = api.schema.relationshiptype.GetRelationshipTypeByNameRequest;
    import RelationshipTypeName = api.schema.relationshiptype.RelationshipTypeName;
    import ContentDeletedEvent = api.content.event.ContentDeletedEvent;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;
    import FocusSwitchEvent = api.ui.FocusSwitchEvent;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Option = api.ui.selector.Option;
    import Deferred = Q.Deferred;
    import ContentUpdatedEvent = api.content.event.ContentUpdatedEvent;
    import ContentServerEventsHandler = api.content.event.ContentServerEventsHandler;

    export class ContentSelector extends api.form.inputtype.support.BaseInputTypeManagingAdd<api.content.ContentId> {

        private config: api.content.form.inputtype.ContentInputTypeViewContext;

        private relationshipTypeName: api.schema.relationshiptype.RelationshipTypeName;

        private contentComboBox: api.content.ContentComboBox;

        private draggingIndex: number;

        private relationshipType: string;

        private allowedContentTypes: string[];

        private allowedContentPaths: string[];

        private contentDeletedListener: (event: ContentDeletedEvent) => void;

        private static contentIdBatch: ContentId[] = [];

        private static loadSummariesResult: Deferred<ContentSummary[]>;

        private static loadSummaries: () => void = api.util.AppHelper.debounce(
            ContentSelector.doFetchSummaries,
            10, false);

        constructor(config?: api.content.form.inputtype.ContentInputTypeViewContext) {
            super('relationship');
            this.addClass('input-type-view');
            this.config = config;
            this.readConfig(config.inputConfig);
            this.handleContentDeletedEvent();
            this.handleContentUpdatedEvent();
        }

        public getContentComboBox(): ContentComboBox {
            return this.contentComboBox;
        }

        private handleContentUpdatedEvent() {
            let contentUpdatedOrMovedListener = (statuses: ContentSummaryAndCompareStatus[], oldPaths?: ContentPath[]) => {

                if (this.contentComboBox.getSelectedOptions().length == 0) {
                    return;
                }

                statuses.forEach((status, index) => {
                    let option;
                    if (oldPaths) {
                        option = this.findOptionByContentPath(oldPaths[index]);
                    } else {
                        option = this.findOptionByContentId(status.getContentId());
                    }
                    if (option) {
                        this.contentComboBox.updateOption(option, status.getContentSummary());
                    }
                });
            };

            let handler = ContentServerEventsHandler.getInstance();
            handler.onContentMoved(contentUpdatedOrMovedListener);
            handler.onContentRenamed(contentUpdatedOrMovedListener);
            handler.onContentUpdated(contentUpdatedOrMovedListener);

            this.onRemoved(event => {
                handler.unContentUpdated(contentUpdatedOrMovedListener);
                handler.unContentRenamed(contentUpdatedOrMovedListener);
                handler.unContentMoved(contentUpdatedOrMovedListener);
            });
        }

        private findOptionByContentPath(contentPath: ContentPath): Option<ContentSummary> {
            let selectedOptions = this.contentComboBox.getSelectedOptions();
            for (let i = 0; i < selectedOptions.length; i++) {
                let option = selectedOptions[i].getOption();
                if (contentPath.equals(option.displayValue.getPath())) {
                    return option;
                }
            }
            return null;
        }

        private findOptionByContentId(contentId: ContentId): Option<ContentSummary> {
            let selectedOptions = this.contentComboBox.getSelectedOptions();
            for (let i = 0; i < selectedOptions.length; i++) {
                let option = selectedOptions[i].getOption();
                if (contentId.equals(option.displayValue.getContentId())) {
                    return option;
                }
            }
            return null;
        }

        private handleContentDeletedEvent() {
            this.contentDeletedListener = (event) => {
                if (this.contentComboBox.getSelectedOptionView().count() === 0) {
                    return;
                }

                let selectedContentIdsMap: {} = {};
                this.contentComboBox.getSelectedOptionView().getSelectedOptions().forEach(
                    (selectedOption: any) => {
                        if (!!selectedOption.getOption().displayValue && !!selectedOption.getOption().displayValue.getContentId()) {
                            selectedContentIdsMap[selectedOption.getOption().displayValue.getContentId().toString()] = '';
                        }
                    });

                event.getDeletedItems().filter(deletedItem => !deletedItem.isPending() &&
                                                              selectedContentIdsMap.hasOwnProperty(
                                                                  deletedItem.getContentId().toString())).forEach((deletedItem) => {
                    let option = this.contentComboBox.getSelectedOptionView().getById(deletedItem.getContentId().toString());
                    if (option != null) {
                        this.contentComboBox.getSelectedOptionView().removeOption(option.getOption(), false);
                    }
                });
            };

            ContentDeletedEvent.on(this.contentDeletedListener);

            this.onRemoved((event) => {
                ContentDeletedEvent.un(this.contentDeletedListener);
            });
        }

        private readConfig(inputConfig: {[element: string]: {[name: string]: string}[];}): void {
            let relationshipTypeConfig = inputConfig['relationshipType'] ? inputConfig['relationshipType'][0] : {};
            this.relationshipType = relationshipTypeConfig['value'];

            if (this.relationshipType) {
                this.relationshipTypeName = new RelationshipTypeName(this.relationshipType);
            } else {
                this.relationshipTypeName = RelationshipTypeName.REFERENCE;
            }

            let allowContentTypeConfig = inputConfig['allowContentType'] || [];
            this.allowedContentTypes = allowContentTypeConfig.map((cfg) => cfg['value']).filter((val) => !!val);

            let allowContentPathConfig = inputConfig['allowPath'] || [];
            this.allowedContentPaths = allowContentPathConfig.map((cfg) => cfg['value']).filter((val) => !!val);
        }

        availableSizeChanged() {
            console.log('Relationship.availableSizeChanged(' + this.getEl().getWidth() + 'x' + this.getEl().getWidth() + ')');
        }

        getValueType(): ValueType {
            return ValueTypes.REFERENCE;
        }

        newInitialValue(): Value {
            return null;
        }

        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {
            if (!ValueTypes.REFERENCE.equals(propertyArray.getType())) {
                propertyArray.convertValues(ValueTypes.REFERENCE);
            }
            super.layout(input, propertyArray);

            const contentSelectorLoader = ContentSelectorLoader.create().setContent(this.config.content).setInputName(
                input.getName()).setAllowedContentPaths(this.allowedContentPaths).setContentTypeNames(
                this.allowedContentTypes).setRelationshipType(this.relationshipType).build();

            const comboboxValue = this.getValueFromPropertyArray(propertyArray);

            this.contentComboBox = api.content.ContentComboBox.create()
                .setName(input.getName())
                .setMaximumOccurrences(input.getOccurrences().getMaximum())
                .setLoader(contentSelectorLoader)
                .setValue(comboboxValue)
                .setRemoveMissingSelectedOptions(true)
                .build();

            this.contentComboBox.getComboBox().onContentMissing((ids: string[]) => {
                ids.forEach(id => this.removePropertyWithId(id));
                this.validate(false);
            });

            return new GetRelationshipTypeByNameRequest(this.relationshipTypeName).sendAndParse()
                .then((relationshipType: api.schema.relationshiptype.RelationshipType) => {

                    this.contentComboBox.setInputIconUrl(relationshipType.getIconUrl());

                    this.appendChild(this.contentComboBox);

                    const contentIds: ContentId[] = [];
                    propertyArray.forEach((property: Property) => {
                        if (property.hasNonNullValue()) {
                            let referenceValue = property.getReference();
                            if (referenceValue instanceof api.util.Reference) {
                                contentIds.push(ContentId.fromReference(referenceValue));
                            }
                        }
                    });

                    return this.doLoadContent(contentIds).then((contents: api.content.ContentSummary[]) => {

                        //TODO: original value doesn't work because of additional request, so have to select manually
                        contents.forEach((content: api.content.ContentSummary) => {
                            this.contentComboBox.select(content);
                        });

                        this.contentComboBox.getSelectedOptions().forEach((selectedOption: SelectedOption<ContentSummary>) => {
                            this.updateSelectedOptionIsEditable(selectedOption);
                        });

                        this.contentComboBox.onOptionSelected((event: SelectedOptionEvent<api.content.ContentSummary>) => {
                            this.fireFocusSwitchEvent(event);

                            const reference = api.util.Reference.from(event.getSelectedOption().getOption().displayValue.getContentId());

                            const value = new Value(reference, ValueTypes.REFERENCE);
                            if (this.contentComboBox.countSelected() === 1) { // overwrite initial value
                                this.getPropertyArray().set(0, value);
                            } else if (!this.getPropertyArray().containsValue(value)) {
                                this.getPropertyArray().add(value);
                            }

                            this.updateSelectedOptionIsEditable(event.getSelectedOption());
                            this.refreshSortable();
                            this.updateSelectedOptionStyle();
                            this.validate(false);
                        });

                        this.contentComboBox.onOptionDeselected((event: SelectedOptionEvent<api.content.ContentSummary>) => {

                            this.getPropertyArray().remove(event.getSelectedOption().getIndex());
                            this.updateSelectedOptionStyle();
                            this.validate(false);
                        });

                        this.setupSortable();

                        this.setLayoutInProgress(false);
                    });
                });
        }

        private removePropertyWithId(id: string) {
            let length = this.getPropertyArray().getSize();
            for (let i = 0; i < length; i++) {
                if (this.getPropertyArray().get(i).getValue().getString() === id) {
                    this.getPropertyArray().remove(i);
                    api.notify.NotifyManager.get().showWarning('Failed to load content item with id ' + id +
                                                               '. The reference will be removed upon save.');
                    break;
                }
            }
        }

        update(propertyArray: api.data.PropertyArray, unchangedOnly: boolean): Q.Promise<void> {
            return super.update(propertyArray, unchangedOnly).then(() => {
                if (!unchangedOnly || !this.contentComboBox.isDirty()) {
                    let value = this.getValueFromPropertyArray(propertyArray);
                    this.contentComboBox.setValue(value);
                }
            });
        }

        reset() {
            this.contentComboBox.resetBaseValues();
        }

        private static doFetchSummaries() {
            new api.content.resource.GetContentSummaryByIds(ContentSelector.contentIdBatch).sendAndParse().then(
                (result: api.content.ContentSummary[]) => {

                    ContentSelector.contentIdBatch = []; // empty batch of ids after loading

                    ContentSelector.loadSummariesResult.resolve(result);

                    ContentSelector.loadSummariesResult = null; // empty loading result after resolving
                });
        }

        private doLoadContent(contentIds: ContentId[]): wemQ.Promise<api.content.ContentSummary[]> {

            ContentSelector.contentIdBatch = ContentSelector.contentIdBatch.concat(contentIds);

            if (!ContentSelector.loadSummariesResult) {
                ContentSelector.loadSummariesResult = wemQ.defer<ContentSummary[]>();
            }

            ContentSelector.loadSummaries();

            return ContentSelector.loadSummariesResult.promise.then((result: api.content.ContentSummary[]) => {
                let contentIdsStr = contentIds.map(id => id.toString());
                return result.filter(content => contentIdsStr.indexOf(content.getId()) >= 0);
            });
        }

        private setupSortable() {
            wemjq(this.getHTMLElement()).find('.selected-options').sortable({
                axis: 'y',
                containment: 'parent',
                handle: '.drag-control',
                tolerance: 'pointer',
                start: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDStart(event, ui),
                update: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDUpdate(event, ui)
            });

            this.updateSelectedOptionStyle();
        }

        private handleDnDStart(event: Event, ui: JQueryUI.SortableUIParams): void {

            let draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
            this.draggingIndex = draggedElement.getSiblingIndex();

            ui.placeholder.html('Drop form item set here');
        }

        private handleDnDUpdate(event: Event, ui: JQueryUI.SortableUIParams) {

            if (this.draggingIndex >= 0) {
                let draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
                let draggedToIndex = draggedElement.getSiblingIndex();
                this.getPropertyArray().move(this.draggingIndex, draggedToIndex);
            }

            this.draggingIndex = -1;
        }

        private updateSelectedOptionStyle() {
            if (this.getPropertyArray().getSize() > 1) {
                this.addClass('multiple-occurrence').removeClass('single-occurrence');
            } else {
                this.addClass('single-occurrence').removeClass('multiple-occurrence');
            }
        }

        private updateSelectedOptionIsEditable(selectedOption: SelectedOption<ContentSummary>) {
            let selectedContentId = selectedOption.getOption().displayValue.getContentId();
            let refersToItself = selectedContentId.toString() === this.config.content.getId();
            selectedOption.getOptionView().toggleClass('non-editable', refersToItself);
        }

        private refreshSortable() {
            wemjq(this.getHTMLElement()).find('.selected-options').sortable('refresh');
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

    api.form.inputtype.InputTypeManager.register(new api.Class('ContentSelector', ContentSelector));
}
