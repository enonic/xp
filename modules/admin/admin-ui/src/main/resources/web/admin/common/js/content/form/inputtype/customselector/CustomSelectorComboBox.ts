module api.content.form.inputtype.customselector {

    import RichComboBox = api.ui.selector.combobox.RichComboBox;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;
    import BaseSelectedOptionView = api.ui.selector.combobox.BaseSelectedOptionView;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import RichComboBoxBuilder = api.ui.selector.combobox.RichComboBoxBuilder;

    export class CustomSelectorComboBox extends RichComboBox<CustomSelectorItem> {

        constructor(input: api.form.Input, requestPath: string, value: string) {
            var loader = new CustomSelectorLoader(requestPath);

            var builder = new RichComboBoxBuilder<CustomSelectorItem>()
                .setComboBoxName(input.getName())
                .setMaximumOccurrences(input.getOccurrences().getMaximum())
                .setOptionDisplayValueViewer(new CustomSelectorItemViewer())
                .setSelectedOptionsView(new CustomSelectorSelectedOptionsView())
                .setDelayedInputValueChangedHandling(300)
                .setLoader(loader)
                .setValue(value);

            super(builder);
        }
    }

    class CustomSelectorSelectedOptionsView extends BaseSelectedOptionsView<CustomSelectorItem> {
        createSelectedOption(option: Option<CustomSelectorItem>): SelectedOption<CustomSelectorItem> {
            return new SelectedOption<CustomSelectorItem>(new CustomSelectorSelectedOptionView(option), this.count());
        }

    }

    class CustomSelectorSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<CustomSelectorItem> {

        constructor(option: Option<CustomSelectorItem>) {
            super(
                new api.ui.selector.combobox.RichSelectedOptionViewBuilder<CustomSelectorItem>(option)
                    .setDraggable(true)
            );
        }

        protected createView(content: CustomSelectorItem): CustomSelectorItemViewer {
            let viewer = new CustomSelectorItemViewer();
            viewer.setObject(this.getOption().displayValue);

            return viewer;
        }

    }

}
