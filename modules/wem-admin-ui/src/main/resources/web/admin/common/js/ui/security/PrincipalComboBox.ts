module api.ui.security {

    import Principal = api.security.Principal;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import BaseSelectedOptionView = api.ui.selector.combobox.BaseSelectedOptionView;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;

    export class PrincipalComboBox extends api.ui.selector.combobox.RichComboBox<Principal> {
        constructor() {
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<Principal>().
                setMaximumOccurrences(0).
                setComboBoxName("principalSelector").
                setIdentifierMethod("getKey").
                setLoader(new PrincipalLoader()).
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
        }

        setOption(option: api.ui.selector.Option<Principal>) {
            this.option = option;
            this.setObject(option.displayValue);
        }

        getOption(): api.ui.selector.Option<Principal> {
            return this.option;
        }

        onSelectedOptionRemoveRequest(listener: {(): void}) {
        }

        unSelectedOptionRemoveRequest(listener: {(): void}) {
        }

    }

    class PrincipalSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<Principal> {

        createSelectedOption(option: Option<Principal>): SelectedOption<Principal> {
            var optionView = new PrincipalSelectedOptionView(option);
            return new api.ui.selector.combobox.SelectedOption<Principal>(optionView, this.count());
        }

    }

}