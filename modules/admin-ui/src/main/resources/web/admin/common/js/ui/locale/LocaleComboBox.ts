module api.ui.locale {

    import Option = api.ui.selector.Option;
    import Locale = api.locale.Locale;
    import LocaleLoader = api.locale.LocaleLoader;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import BaseSelectedOptionView = api.ui.selector.combobox.BaseSelectedOptionView;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;

    export class LocaleComboBox extends api.ui.selector.combobox.RichComboBox<Locale> {
        constructor(max?: number) {
            var localeSelectedOptionsView = new LocaleSelectedOptionsView();
            localeSelectedOptionsView.onOptionDeselected(() => {
                this.clearSelection();
            });
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<Locale>().
                setMaximumOccurrences(max || 0).
                setComboBoxName("localeSelector").
                setIdentifierMethod("getTag").
                setLoader(new LocaleLoader()).
                setSelectedOptionsView(localeSelectedOptionsView).
                setOptionDisplayValueViewer(new LocaleViewer()).
                setDelayedInputValueChangedHandling(500);
            super(builder);
        }

        clearSelection(ignoreEmpty: boolean = false) {
            this.getLoader().search("");
            super.clearSelection(ignoreEmpty);
        }
    }


    class LocaleSelectedOptionView extends LocaleViewer implements api.ui.selector.combobox.SelectedOptionView<Locale> {

        private option: Option<Locale>;

        constructor(option: Option<Locale>) {
            super();
            this.setOption(option);
            this.setClass("locale-selected-option-view");
            var removeButton = new api.dom.AEl("icon-close");
            removeButton.onClicked((event: MouseEvent) => this.notifyRemoveClicked(event));
            this.appendChild(removeButton);
        }

        setOption(option: api.ui.selector.Option<Locale>) {
            this.option = option;
            this.setObject(option.displayValue);
        }

        getOption(): api.ui.selector.Option<Locale> {
            return this.option;
        }

        onSelectedOptionRemoveRequest(listener: {(): void}) {
            this.onRemoveClicked(listener);
        }

        unSelectedOptionRemoveRequest(listener: {(): void}) {
            this.unRemoveClicked(listener);
        }

    }

    class LocaleSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<Locale> {

        constructor() {
            super("locale-selected-options-view");
        }

        createSelectedOption(option: Option<Locale>): SelectedOption<Locale> {
            var optionView = new LocaleSelectedOptionView(option);
            return new api.ui.selector.combobox.SelectedOption<Locale>(optionView, this.count());
        }

    }

}