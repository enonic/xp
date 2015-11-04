module api.content {

    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Option = api.ui.selector.Option;
    import RichComboBox = api.ui.selector.combobox.RichComboBox;
    import RichComboBoxBuilder = api.ui.selector.combobox.RichComboBoxBuilder;

    export class ContentComboBox extends RichComboBox<ContentSummary> {

        constructor(builder: ContentComboBoxBuilder) {

            var loader = builder.loader ? builder.loader : new ContentSummaryLoader();
            loader.setAllowedContentTypes(builder.allowedContentTypes);

            var richComboBoxBuilder = new RichComboBoxBuilder<ContentSummary>().
                setComboBoxName(builder.name ? builder.name : 'contentSelector').
                setLoader(loader).
                setSelectedOptionsView(new ContentSelectedOptionsView()).
                setMaximumOccurrences(builder.maximumOccurrences).
                setOptionDisplayValueViewer(new api.content.ContentSummaryViewer()).
                setDelayedInputValueChangedHandling(750).
                setMinWidth(builder.minWidth);

            super(richComboBoxBuilder);

            this.addClass('content-combo-box');
        }

        getContent(contentId: ContentId): ContentSummary {
            var option = this.getOptionByValue(contentId.toString());
            if (option) {
                return option.displayValue;
            }
            return null;
        }

        setContent(content: ContentSummary) {

            this.clearSelection();
            if (content) {
                var optionToSelect: Option<ContentSummary> = this.getOptionByValue(content.getContentId().toString());
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
            var optionView = new ContentSelectedOptionView(option);
            return new SelectedOption<ContentSummary>(optionView, this.count());
        }
    }

    export class ContentSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<ContentSummary> {


        constructor(option: api.ui.selector.Option<ContentSummary>) {
            super(option);
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

        createActionButtons(content: ContentSummary): api.dom.Element[] {
            let editButton = new api.dom.AEl("edit");
            editButton.onClicked((event: Event) => {
                new api.content.EditContentEvent([content]).fire();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            return [editButton];
        }

    }

    export class ContentComboBoxBuilder {

        name: string;

        maximumOccurrences: number = 0;

        loader: ContentSummaryLoader;

        allowedContentTypes: string[];

        minWidth: number;

        setName(value: string): ContentComboBoxBuilder {
            this.name = value;
            return this;
        }

        setMaximumOccurrences(maximumOccurrences: number): ContentComboBoxBuilder {
            this.maximumOccurrences = maximumOccurrences;
            return this;
        }

        setLoader(loader: ContentSummaryLoader): ContentComboBoxBuilder {
            this.loader = loader;
            return this;
        }

        setAllowedContentTypes(allowedTypes: string[]): ContentComboBoxBuilder {
            this.allowedContentTypes = allowedTypes;
            return this;
        }

        setMinWidth(value: number): ContentComboBoxBuilder {
            this.minWidth = value;
            return this;
        }

        build(): ContentComboBox {
            return new ContentComboBox(this);
        }

    }
}