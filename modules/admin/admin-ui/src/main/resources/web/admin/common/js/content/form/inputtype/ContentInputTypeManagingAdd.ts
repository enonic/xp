module api.content.form.inputtype {

    import RichComboBox = api.ui.selector.combobox.RichComboBox;
    import RelationshipTypeName = api.schema.relationshiptype.RelationshipTypeName;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentServerEventsHandler = api.content.event.ContentServerEventsHandler;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import ContentDeletedEvent = api.content.event.ContentDeletedEvent;
    import ContentServerChangeItem = api.content.event.ContentServerChangeItem;
    import SelectedOptionsView = api.ui.selector.combobox.SelectedOptionsView;
    import BaseInputTypeManagingAdd = api.form.inputtype.support.BaseInputTypeManagingAdd;
    import Option = api.ui.selector.Option;

    export class ContentInputTypeManagingAdd<RAW_VALUE_TYPE> extends BaseInputTypeManagingAdd<RAW_VALUE_TYPE> {

        protected config: api.content.form.inputtype.ContentInputTypeViewContext;

        protected relationshipTypeName: RelationshipTypeName;

        protected relationshipType: string;

        protected allowedContentTypes: string[];

        protected allowedContentPaths: string[];

        protected contentDeletedListener: (paths: ContentServerChangeItem[], pending?: boolean) => void;

        constructor(className?: string, config?: api.content.form.inputtype.ContentInputTypeViewContext) {
            super(className);
            this.addClass('input-type-view');
            this.config = config;

            this.readConfig(config.inputConfig);

            this.handleContentDeletedEvent();
            this.handleContentUpdatedEvent();
        }

        protected getContentComboBox(): RichComboBox<any> {
            throw new Error('Should be overridden by inheritor');
        }

        protected getContentPath(raw: RAW_VALUE_TYPE): ContentPath {
            throw new Error('Should be overridden by inheritor');
        }

        protected getSelectedOptions(): SelectedOption<RAW_VALUE_TYPE>[] {
            return this.getSelectedOptionsView().getSelectedOptions();
        }

        protected getSelectedOptionsView(): SelectedOptionsView<RAW_VALUE_TYPE> {
            return this.getContentComboBox().getSelectedOptionView();
        }

        protected readConfig(inputConfig: {[element: string]: {[name: string]: string}[];}): void {
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

        private handleContentUpdatedEvent() {
            let contentUpdatedOrMovedListener = (statuses: ContentSummaryAndCompareStatus[], oldPaths?: ContentPath[]) => {

                if (this.getSelectedOptions().length == 0) {
                    return;
                }

                statuses.forEach((status, index) => {
                    let selectedOption;
                    if (oldPaths) {
                        selectedOption = this.findSelectedOptionByContentPath(oldPaths[index]);
                    } else {
                        selectedOption = this.getSelectedOptionsView().getById(status.getContentId().toString());
                    }
                    if (selectedOption) {
                        this.getContentComboBox().updateOption(selectedOption.getOption(), status.getContentSummary());
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

        private findSelectedOptionByContentPath(contentPath: ContentPath): SelectedOption<RAW_VALUE_TYPE> {
            let selectedOptions = this.getSelectedOptions();
            for (let i = 0; i < selectedOptions.length; i++) {
                let option = selectedOptions[i];
                if (contentPath.equals(this.getContentPath(option.getOption().displayValue))) {
                    return option;
                }
            }
            return null;
        }

        private handleContentDeletedEvent() {
            this.contentDeletedListener = (paths: ContentServerChangeItem[], pending?: boolean) => {
                if (this.getSelectedOptions().length == 0) {
                    return;
                }

                let selectedContentIdsMap: {} = {};
                this.getSelectedOptions().forEach((selectedOption: any) => {
                    if (!!selectedOption.getOption().displayValue && !!selectedOption.getOption().displayValue.getContentId()) {
                        selectedContentIdsMap[selectedOption.getOption().displayValue.getContentId().toString()] = '';
                    }
                });

                paths.filter(deletedItem => !pending && selectedContentIdsMap.hasOwnProperty(deletedItem.getContentId().toString()))
                    .forEach((deletedItem) => {
                        let option = this.getSelectedOptionsView().getById(deletedItem.getContentId().toString());
                        if (option != null) {
                            this.getSelectedOptionsView().removeOption(option.getOption(), false);
                        }
                    });
            };

            let handler = ContentServerEventsHandler.getInstance();
            handler.onContentDeleted(this.contentDeletedListener);

            this.onRemoved((event) => {
                handler.unContentDeleted(this.contentDeletedListener);
            });
        }
    }
}
