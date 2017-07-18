module api.content.image {

    import ContentSummaryLoader = api.content.resource.ContentSummaryLoader;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Option = api.ui.selector.Option;
    import RichComboBox = api.ui.selector.combobox.RichComboBox;
    import RichComboBoxBuilder = api.ui.selector.combobox.RichComboBoxBuilder;
    import ContentQueryResultJson = api.content.json.ContentQueryResultJson;
    import ContentSummaryJson = api.content.json.ContentSummaryJson;
    import BaseLoader = api.util.loader.BaseLoader;
    import OptionDataLoader = api.ui.selector.OptionDataLoader;
    import SelectedOptionsView = api.ui.selector.combobox.SelectedOptionsView;
    import ContentTypeName = api.schema.content.ContentTypeName;

    export class ImageContentComboBox extends RichComboBox<any> {

        constructor(builder: ImageContentComboBoxBuilder) {

            let loader = builder.loader ? builder.loader : new ContentSummaryLoader();

            if (!builder.optionDataLoader) {
                builder.setOptionDataLoader(ImageOptionDataLoader.create().setContent(builder.content).setContentTypeNames(
                    [ContentTypeName.IMAGE.toString(), ContentTypeName.MEDIA_VECTOR.toString()]).build());
            }

            let richComboBoxBuilder = new RichComboBoxBuilder()
                .setComboBoxName(builder.name ? builder.name : 'imageContentSelector')
                .setLoader(loader).setSelectedOptionsView(builder.selectedOptionsView || new ImageSelectorSelectedOptionsView())
                .setMaximumOccurrences(builder.maximumOccurrences)
                .setOptionDisplayValueViewer(new ImageSelectorViewer())
                .setDelayedInputValueChangedHandling(750)
                .setValue(builder.value)
                .setMinWidth(builder.minWidth)
                .setTreegridDropdownEnabled(builder.treegridDropdownEnabled)
                .setOptionDataLoader(builder.optionDataLoader)
                .setOptionDataHelper(new ContentSummaryOptionDataHelper())
                .setRemoveMissingSelectedOptions(true)
                .setDisplayMissingSelectedOptions(true);

            // Actually the hack.
            // ImageSelectorSelectedOptionsView and BaseSelectedOptionsView<ContentSummary> are incompatible in loaders.
            super(<RichComboBoxBuilder<ImageSelectorDisplayValue>>richComboBoxBuilder);

        }

        createOption(value: ContentSummary): Option<ImageSelectorDisplayValue> {
            return {
                value: this.getDisplayValueId(value),
                displayValue: ImageSelectorDisplayValue.fromContentSummary(value)
            };
        }

        setContent(content: ContentSummary) {

            this.clearSelection();
            if (content) {
                let optionToSelect: Option<ImageSelectorDisplayValue> = this.getOptionByValue(content.getContentId().toString());
                if (!optionToSelect) {
                    optionToSelect = this.createOption(content);
                    this.addOption(optionToSelect);
                }
                this.selectOption(optionToSelect);

            }
        }

        getContent(contentId: ContentId): ContentSummary {
            let option = this.getOptionByValue(contentId.toString());
            if (option) {
                return option.displayValue;
            }
            return null;
        }

        getOptionDataLoader(): ImageOptionDataLoader {
            return <ImageOptionDataLoader>super.getOptionDataLoader();
        }

        public static create(): ImageContentComboBoxBuilder {
            return new ImageContentComboBoxBuilder();
        }
    }

    export class ImageContentComboBoxBuilder {

        name: string;

        maximumOccurrences: number = 0;

        loader: BaseLoader<ContentQueryResultJson<ContentSummaryJson>, ContentSummary>;

        minWidth: number;

        selectedOptionsView: SelectedOptionsView<any>;

        optionDisplayValueViewer: ImageSelectorViewer;

        optionDataLoader: OptionDataLoader<any>;

        treegridDropdownEnabled: boolean;

        value: string;

        content: ContentSummary;

        setContent(value: ContentSummary): ImageContentComboBoxBuilder {
            this.content = value;
            return this;
        }

        setName(value: string): ImageContentComboBoxBuilder {
            this.name = value;
            return this;
        }

        setValue(value: string): ImageContentComboBoxBuilder {
            this.value = value;
            return this;
        }

        setMaximumOccurrences(maximumOccurrences: number): ImageContentComboBoxBuilder {
            this.maximumOccurrences = maximumOccurrences;
            return this;
        }

        setLoader(loader: BaseLoader<ContentQueryResultJson<ContentSummaryJson>, ContentSummary>): ImageContentComboBoxBuilder {
            this.loader = loader;
            return this;
        }

        setMinWidth(value: number): ImageContentComboBoxBuilder {
            this.minWidth = value;
            return this;
        }

        setSelectedOptionsView(value: SelectedOptionsView<any>): ImageContentComboBoxBuilder {
            this.selectedOptionsView = value;
            return this;
        }

        setOptionDisplayValueViewer(value: ImageSelectorViewer): ImageContentComboBoxBuilder {
            this.optionDisplayValueViewer = value;
            return this;
        }

        setOptionDataLoader(value: OptionDataLoader<any>): ImageContentComboBoxBuilder {
            this.optionDataLoader = value;
            return this;
        }

        setTreegridDropdownEnabled(value: boolean): ImageContentComboBoxBuilder {
            this.treegridDropdownEnabled = value;
            return this;
        }

        build(): ImageContentComboBox {
            return new ImageContentComboBox(this);
        }

    }
}
