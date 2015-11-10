module api.ui.security {

    import Option = api.ui.selector.Option;
    import Principal = api.security.Principal;
    import PrincipalLoader = api.security.PrincipalLoader;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import BaseSelectedOptionView = api.ui.selector.combobox.BaseSelectedOptionView;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;

    export class PrincipalComboBox extends api.ui.selector.combobox.RichComboBox<Principal> {
        constructor(loader?: PrincipalLoader, max?: number) {
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<Principal>().
                setMaximumOccurrences(max || 0).
                setComboBoxName("principalSelector").
                setIdentifierMethod("getKey").
                setLoader(loader || new PrincipalLoader()).
                setSelectedOptionsView(new PrincipalSelectedOptionsView()).
                setOptionDisplayValueViewer(new PrincipalViewer()).
                setDelayedInputValueChangedHandling(500);
            super(builder);
        }
    }


    class PrincipalSelectedOptionView extends PrincipalViewer implements api.ui.selector.combobox.SelectedOptionView<Principal> {

        private option: Option<Principal>;

        constructor(option: Option<Principal>) {
            super();
            this.setOption(option);
            this.setClass("principal-selected-option-view");
            var removeButton = new api.dom.AEl("icon-close");
            removeButton.onClicked((event: MouseEvent) => this.notifyRemoveClicked(event));
            this.appendChild(removeButton);
        }

        setOption(option: api.ui.selector.Option<Principal>) {
            this.option = option;
            this.setObject(option.displayValue);
        }

        getOption(): api.ui.selector.Option<Principal> {
            return this.option;
        }

    }

    class PrincipalSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<Principal> {

        constructor() {
            super("principal-selected-options-view");
        }

        createSelectedOption(option: Option<Principal>): SelectedOption<Principal> {
            var optionView = new PrincipalSelectedOptionView(option);
            return new api.ui.selector.combobox.SelectedOption<Principal>(optionView, this.count());
        }

    }

}