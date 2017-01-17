module api.ui.security {

    import Option = api.ui.selector.Option;
    import Principal = api.security.Principal;
    import PrincipalLoader = api.security.PrincipalLoader;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import BaseSelectedOptionView = api.ui.selector.combobox.BaseSelectedOptionView;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;
    import PrincipalKey = api.security.PrincipalKey;

    export class PrincipalComboBox extends api.ui.selector.combobox.RichComboBox<Principal> {
        constructor(builder: PrincipalComboBoxBuilder) {
            let richComboBoxBuilder = new api.ui.selector.combobox.RichComboBoxBuilder<Principal>().
            setMaximumOccurrences(builder.maxOccurrences).
            setComboBoxName('principalSelector').
            setIdentifierMethod('getKey').
            setLoader(builder.loader).
            setValue(builder.value).
            setDisplayMissingSelectedOptions(builder.displayMissing).
            setSelectedOptionsView(new PrincipalSelectedOptionsView()).
            setOptionDisplayValueViewer(new PrincipalViewer()).
            setDelayedInputValueChangedHandling(500);

            super(richComboBoxBuilder);
        }

        static create(): PrincipalComboBoxBuilder {
            return new PrincipalComboBoxBuilder();
        }
    }

    export class PrincipalComboBoxBuilder {

        loader: PrincipalLoader = new PrincipalLoader();

        maxOccurrences: number = 0;

        value: string;

        displayMissing: boolean = false;

        setLoader(value: PrincipalLoader): PrincipalComboBoxBuilder {
            this.loader = value;
            return this;
        }

        setMaxOccurences(value: number): PrincipalComboBoxBuilder {
            this.maxOccurrences = value;
            return this;
        }

        setValue(value: string): PrincipalComboBoxBuilder {
            this.value = value;
            return this;
        }

        setDisplayMissing(value: boolean): PrincipalComboBoxBuilder {
            this.displayMissing = value;
            return this;
        }

        build(): PrincipalComboBox {
            return new PrincipalComboBox(this);
        }
    }

    export class PrincipalSelectedOptionView extends PrincipalViewer implements api.ui.selector.combobox.SelectedOptionView<Principal> {

        private option: Option<Principal>;

        constructor(option: Option<Principal>) {
            super();
            this.setOption(option);
            this.setClass('principal-selected-option-view');
            let removeButton = new api.dom.AEl('icon-close');
            removeButton.onClicked((event: MouseEvent) => {
                this.notifyRemoveClicked(event);
                event.stopPropagation();
                event.preventDefault();
                return false;
            });
            this.appendChild(removeButton);
        }

        setEditable(editable: boolean) {
            // must be implemented by children
        }

        setOption(option: api.ui.selector.Option<Principal>) {
            this.option = option;
            this.setObject(option.displayValue);
        }

        getOption(): api.ui.selector.Option<Principal> {
            return this.option;
        }

    }

    export class PrincipalSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<Principal> {

        constructor() {
            super('principal-selected-options-view');
        }

        createSelectedOption(option: Option<Principal>, isEmpty?: boolean): SelectedOption<Principal> {
            let optionView = !option.empty ? new PrincipalSelectedOptionView(option) : new RemovedPrincipalSelectedOptionView(option);
            return new api.ui.selector.combobox.SelectedOption<Principal>(optionView, this.count());
        }

        makeEmptyOption(id: string): Option<Principal> {

            let key = PrincipalKey.fromString(id);

            return <Option<Principal>>{
                value: id,
                displayValue: Principal.create().setDisplayName(key.getId()).
                setKey(key).build(),
                empty: true
            };
        }

    }

    export class RemovedPrincipalSelectedOptionView extends PrincipalSelectedOptionView {

        constructor(option: Option<Principal>) {
            super(option);
            this.addClass('removed');
        }

        resolveSubName(object: Principal, relativePath: boolean = false): string {
            return 'This user is deleted';
        }
    }

}
