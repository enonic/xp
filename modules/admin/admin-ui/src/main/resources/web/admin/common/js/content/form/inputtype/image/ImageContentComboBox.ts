module api.content.form.inputtype.image {

    import ContentSummaryLoader = api.content.ContentSummaryLoader;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Option = api.ui.selector.Option;
    import RichComboBox = api.ui.selector.combobox.RichComboBox;
    import RichComboBoxBuilder = api.ui.selector.combobox.RichComboBoxBuilder;
    import ImageSelectorDisplayValue = api.content.form.inputtype.image.ImageSelectorDisplayValue;
    import ImageSelectorViewer = api.content.form.inputtype.image.ImageSelectorViewer;
    import ImageSelectorSelectedOptionsView = api.content.form.inputtype.image.ImageSelectorSelectedOptionsView;

    export class ImageContentComboBox extends RichComboBox<ImageSelectorDisplayValue> {

        constructor(builder: ImageContentComboBoxBuilder) {

            var loader = builder.loader ? builder.loader : new ContentSummaryLoader();
            loader.setAllowedContentTypes(builder.allowedContentTypes);

            var richComboBoxBuilder = new RichComboBoxBuilder().
                setComboBoxName(builder.name ? builder.name : 'imageContentSelector').
                setLoader(loader).
                setSelectedOptionsView(builder.selectedOptionsView || new ImageSelectorSelectedOptionsView()).
                setMaximumOccurrences(builder.maximumOccurrences).
                setOptionDisplayValueViewer(new ImageSelectorViewer()).
                setDelayedInputValueChangedHandling(750).
                setMinWidth(builder.minWidth);

            // Actually the hack.
            // ImageSelectorSelectedOptionsView and BaseSelectedOptionsView<ContentSummary> are incompatible in loaders.
            super(<RichComboBoxBuilder<ImageSelectorDisplayValue>>richComboBoxBuilder);
        }

        createOption(value: ContentSummary): Option<ImageSelectorDisplayValue> {
            return {
                value: this.getDisplayValueId(value),
                displayValue: ImageSelectorDisplayValue.fromContentSummary(value)
            }
        }

        public static create(): ImageContentComboBoxBuilder {
            return new ImageContentComboBoxBuilder();
        }
    }

    export class ImageContentComboBoxBuilder {

        name: string;

        maximumOccurrences: number = 0;

        loader: ContentSummaryLoader;

        allowedContentTypes: string[];

        minWidth: number;

        selectedOptionsView: ImageSelectorSelectedOptionsView;

        optionDisplayValueViewer: ImageSelectorViewer;

        setName(value: string): ImageContentComboBoxBuilder {
            this.name = value;
            return this;
        }

        setMaximumOccurrences(maximumOccurrences: number): ImageContentComboBoxBuilder {
            this.maximumOccurrences = maximumOccurrences;
            return this;
        }

        setLoader(loader: ContentSummaryLoader): ImageContentComboBoxBuilder {
            this.loader = loader;
            return this;
        }

        setAllowedContentTypes(allowedTypes: string[]): ImageContentComboBoxBuilder {
            this.allowedContentTypes = allowedTypes;
            return this;
        }

        setMinWidth(value: number): ImageContentComboBoxBuilder {
            this.minWidth = value;
            return this;
        }

        setSelectedOptionsView(value: ImageSelectorSelectedOptionsView): ImageContentComboBoxBuilder {
            this.selectedOptionsView = value;
            return this;
        }

        setOptionDisplayValueViewer(value: ImageSelectorViewer): ImageContentComboBoxBuilder {
            this.optionDisplayValueViewer = value;
            return this;
        }

        build(): ImageContentComboBox {
            return new ImageContentComboBox(this);
        }

    }
}