module api.macro {

    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Option = api.ui.selector.Option;
    import RichComboBox = api.ui.selector.combobox.RichComboBox;
    import RichComboBoxBuilder = api.ui.selector.combobox.RichComboBoxBuilder;
    import MacrosLoader = api.macro.resource.MacrosLoader;

    export class MacroComboBox extends RichComboBox<MacroDescriptor> {

        protected loader: MacrosLoader;

        constructor(builder: MacroComboBoxBuilder) {

            let richComboBoxBuilder = new RichComboBoxBuilder<MacroDescriptor>().
                setComboBoxName('macroSelector').
                setLoader(builder.loader).
                setSelectedOptionsView(new MacroSelectedOptionsView()).
                setMaximumOccurrences(builder.maximumOccurrences).
                setDelayedInputValueChangedHandling(750).setOptionDisplayValueViewer(new MacroViewer()).
                setValue(builder.value).
                setMaxHeight(250);

            super(richComboBoxBuilder);

            this.addClass('content-combo-box');
        }

        getLoader(): MacrosLoader {
            return this.loader;
        }

        createOption(val: MacroDescriptor): Option<MacroDescriptor> {
            return {
                value: val.getKey().getRefString(),
                displayValue: val
            };
        }

        public static create(): MacroComboBoxBuilder {
            return new MacroComboBoxBuilder();
        }
    }

    export class MacroSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<MacroDescriptor> {

        createSelectedOption(option: api.ui.selector.Option<MacroDescriptor>): SelectedOption<MacroDescriptor> {
            let optionView = new MacroSelectedOptionView(option);
            return new SelectedOption<MacroDescriptor>(optionView, this.count());
        }
    }

    export class MacroSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<MacroDescriptor> {

        constructor(option: api.ui.selector.Option<MacroDescriptor>) {
            super(new api.ui.selector.combobox.RichSelectedOptionViewBuilder<MacroDescriptor>(option));
        }

        resolveIconUrl(macroDescriptor: MacroDescriptor): string {
            return macroDescriptor.getIconUrl();
        }

        resolveTitle(macroDescriptor: MacroDescriptor): string {
            return macroDescriptor.getDisplayName();
        }

        resolveSubTitle(macroDescriptor: MacroDescriptor): string {
            return macroDescriptor.getDescription();
        }
    }

    export class MacroComboBoxBuilder {

        maximumOccurrences: number = 0;

        loader: MacrosLoader;

        value: string;

        setMaximumOccurrences(maximumOccurrences: number): MacroComboBoxBuilder {
            this.maximumOccurrences = maximumOccurrences;
            return this;
        }

        setLoader(loader: MacrosLoader): MacroComboBoxBuilder {
            this.loader = loader;
            return this;
        }

        setValue(value: string): MacroComboBoxBuilder {
            this.value = value;
            return this;
        }

        build(): MacroComboBox {
            return new MacroComboBox(this);
        }

    }
}