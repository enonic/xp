module api.content.form.inputtype.contentselector {

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

    class CustomSelectorSelectedOptionView extends BaseSelectedOptionView<CustomSelectorItem> {


        constructor(option: Option<CustomSelectorItem>) {
            super(option);
        }

        doRender(): wemQ.Promise<boolean> {

            let viewer = new CustomSelectorItemViewer();
            viewer.setObject(this.getOption().displayValue);

            var removeButtonEl = new api.dom.AEl("remove");

            removeButtonEl.onClicked((event: Event) => {
                this.notifyRemoveClicked();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.appendChildren<api.dom.Element>(removeButtonEl, viewer);

            return wemQ(true);
        }

    }

}