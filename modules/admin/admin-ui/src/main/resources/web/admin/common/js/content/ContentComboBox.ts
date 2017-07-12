module api.content {

    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Option = api.ui.selector.Option;
    import RichComboBox = api.ui.selector.combobox.RichComboBox;
    import RichComboBoxBuilder = api.ui.selector.combobox.RichComboBoxBuilder;
    import ContentSummaryLoader = api.content.resource.ContentSummaryLoader;
    import RichSelectedOptionViewBuilder = api.ui.selector.combobox.RichSelectedOptionViewBuilder;
    import ContentQueryResultJson = api.content.json.ContentQueryResultJson;
    import ContentSummaryJson = api.content.json.ContentSummaryJson;
    import OptionDataLoader = api.ui.selector.OptionDataLoader;
    import ContentTreeSelectorItem = api.content.resource.ContentTreeSelectorItem;
    import Viewer = api.ui.Viewer;
    import ContentAndStatusTreeSelectorItem = api.content.resource.ContentAndStatusTreeSelectorItem;
    import TreeNode = api.ui.treegrid.TreeNode;
    import ContentRowFormatter = api.content.util.ContentRowFormatter;
    import GridColumn = api.ui.grid.GridColumn;
    import i18n = api.util.i18n;

    export class ContentComboBox extends RichComboBox<ContentSummary> {

        constructor(builder: ContentComboBoxBuilder) {

            let loader = builder.loader ? builder.loader : new ContentSummaryLoader();

            const treeGridDropdownEnabled = builder.treegridDropdownEnabled == undefined ? true : builder.treegridDropdownEnabled;

            let richComboBoxBuilder = new RichComboBoxBuilder<ContentSummary>()
                .setComboBoxName(builder.name ? builder.name : 'contentSelector')
                .setLoader(loader)
                .setSelectedOptionsView(builder.selectedOptionsView || new ContentSelectedOptionsView())
                .setMaximumOccurrences(builder.maximumOccurrences)
                .setOptionDisplayValueViewer(builder.optionDisplayValueViewer || new api.content.ContentSummaryViewer())
                .setDelayedInputValueChangedHandling(builder.delayedInputValueChangedHandling || 750)
                .setValue(builder.value)
                .setDisplayMissingSelectedOptions(builder.displayMissingSelectedOptions)
                .setRemoveMissingSelectedOptions(builder.removeMissingSelectedOptions)
                .setSkipAutoDropShowOnValueChange(builder.skipAutoDropShowOnValueChange)
                .setTreegridDropdownEnabled(treeGridDropdownEnabled)
                .setOptionDataHelper(builder.optionDataHelper || new ContentSummaryOptionDataHelper())
                .setOptionDataLoader(builder.optionDataLoader ||
                                     ContentSummaryOptionDataLoader.create().setLoadStatus(builder.showStatus).build())
                .setMinWidth(builder.minWidth);

            if(builder.showStatus && treeGridDropdownEnabled) {
                const columns = [new api.ui.grid.GridColumnBuilder().setId('status').setName('Status').setField(
                    'displayValue').setFormatter(
                    ContentRowFormatter.statusSelectorFormatter).setCssClass('status').setBoundaryWidth(75, 75).build()];

                richComboBoxBuilder.setCreateColumns(columns);
            }
            super(richComboBoxBuilder);

            this.addClass('content-combo-box');
        }

        getLoader(): ContentSummaryLoader {
            return <ContentSummaryLoader> this.loader;
        }

        getContent(contentId: ContentId): ContentSummary {
            let option = this.getOptionByValue(contentId.toString());
            if (option) {
                return option.displayValue;
            }
            return null;
        }

        setContent(content: ContentSummary) {

            this.clearSelection();
            if (content) {
                let optionToSelect: Option<ContentSummary> = this.getOptionByValue(content.getContentId().toString());
                if (!optionToSelect) {
                    optionToSelect = {
                        value: content.getContentId().toString(),
                        displayValue: content
                    };
                    this.addOption(optionToSelect);
                }
                this.selectOption(optionToSelect);

            }
        }

        public static create(): ContentComboBoxBuilder {
            return new ContentComboBoxBuilder();
        }
    }

    export class ContentSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<ContentSummary> {

        createSelectedOption(option: api.ui.selector.Option<ContentSummary>): SelectedOption<ContentSummary> {
            let optionView = !!option.displayValue ? new ContentSelectedOptionView(option) : new MissingContentSelectedOptionView(option);
            return new SelectedOption<ContentSummary>(optionView, this.count());
        }
    }

    export class MissingContentSelectedOptionView extends api.ui.selector.combobox.BaseSelectedOptionView<ContentSummary> {

        private id: string;

        constructor(option: api.ui.selector.Option<ContentSummary>) {
            super(option);
            this.id = option.value;
        }

        doRender(): wemQ.Promise<boolean> {

            let removeButtonEl = new api.dom.AEl('remove');
            let message = new api.dom.H6El('missing-content');

            message.setHtml(i18n('field.content.noaccess', this.id));

            removeButtonEl.onClicked((event: Event) => {
                this.notifyRemoveClicked();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.appendChildren<api.dom.Element>(removeButtonEl, message);

            return wemQ(true);
        }
    }

    export class ContentSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<ContentSummary> {

        constructor(option: api.ui.selector.Option<ContentSummary>) {
            super(
                new api.ui.selector.combobox.RichSelectedOptionViewBuilder<ContentSummary>(option)
                    .setEditable(true)
                    .setDraggable(true)
            );
        }

        resolveIconUrl(content: ContentSummary): string {
            return content.getIconUrl();
        }

        resolveTitle(content: ContentSummary): string {
            return content.getDisplayName().toString();
        }

        resolveSubTitle(content: ContentSummary): string {
            return content.getPath().toString();
        }

        protected createEditButton(content: api.content.ContentSummary): api.dom.AEl {
            let editButton = super.createEditButton(content);
            editButton.onClicked((event: Event) => {
                let model = [api.content.ContentSummaryAndCompareStatus.fromContentSummary(content)];
                new api.content.event.EditContentEvent(model).fire();
            });

            return editButton;
        }
    }

    export class ContentComboBoxBuilder extends RichComboBoxBuilder<ContentSummary> {

        name: string;

        maximumOccurrences: number = 0;

        loader: api.util.loader.BaseLoader<ContentQueryResultJson<ContentSummaryJson>, ContentSummary>;

        minWidth: number;

        value: string;

        displayMissingSelectedOptions: boolean;

        removeMissingSelectedOptions: boolean;

        showStatus: boolean = false;

        setName(value: string): ContentComboBoxBuilder {
            this.name = value;
            return this;
        }

        setMaximumOccurrences(maximumOccurrences: number): ContentComboBoxBuilder {
            this.maximumOccurrences = maximumOccurrences;
            return this;
        }

        setLoader(loader: api.util.loader.BaseLoader<ContentQueryResultJson<ContentSummaryJson>, ContentSummary>): ContentComboBoxBuilder {
            this.loader = loader;
            return this;
        }

        setMinWidth(value: number): ContentComboBoxBuilder {
            this.minWidth = value;
            return this;
        }

        setValue(value: string): ContentComboBoxBuilder {
            this.value = value;
            return this;
        }

        setDisplayMissingSelectedOptions(value: boolean): ContentComboBoxBuilder {
            this.displayMissingSelectedOptions = value;
            return this;
        }

        setRemoveMissingSelectedOptions(value: boolean): ContentComboBoxBuilder {
            this.removeMissingSelectedOptions = value;
            return this;
        }

        setTreegridDropdownEnabled(value: boolean): ContentComboBoxBuilder {
            super.setTreegridDropdownEnabled(value);
            return this;
        }

        setShowStatus(value: boolean): ContentComboBoxBuilder {
            this.showStatus = value;
            return this;
        }

        setOptionDisplayValueViewer(value: Viewer<any>): ContentComboBoxBuilder {
            super.setOptionDisplayValueViewer(value);
            return this;
        }

        setOptionDataLoader(value: OptionDataLoader<ContentTreeSelectorItem>): ContentComboBoxBuilder {
            super.setOptionDataLoader(value);
            return this;
        }

        build(): ContentComboBox {
            return new ContentComboBox(this);
        }

    }
}
