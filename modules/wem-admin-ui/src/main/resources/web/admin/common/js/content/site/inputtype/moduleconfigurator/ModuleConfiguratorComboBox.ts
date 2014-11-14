module api.content.site.inputtype.moduleconfigurator {

    import Property = api.data.Property;
    import Module = api.module.Module;
    import ModuleViewer = api.module.ModuleViewer;
    import ModuleLoader = api.module.ModuleLoader;
    import RootDataSet = api.data.RootDataSet;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import SelectedOptionView = api.ui.selector.combobox.SelectedOptionView;
    import SelectedOptionsView = api.ui.selector.combobox.SelectedOptionsView;


    export class ModuleConfiguratorComboBox extends api.ui.selector.combobox.RichComboBox<Module> {

        constructor(maxOccurrences: number, moduleConfigProvider: ModuleConfigProvider) {

            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<Module>();
            builder.
                setMaximumOccurrences(maxOccurrences).
                setIdentifierMethod('getModuleKey').
                setComboBoxName("moduleSelector").
                setLoader(new ModuleLoader()).
                setSelectedOptionsView(new ModuleConfiguratorSelectedOptionsView(moduleConfigProvider)).
                setOptionDisplayValueViewer(new ModuleViewer()).
                setDelayedInputValueChangedHandling(500);

            super(builder);
        }

    }

    export class ModuleConfiguratorSelectedOptionsView extends SelectedOptionsView<Module> {

        private moduleConfigProvider: ModuleConfigProvider;

        constructor(moduleConfigProvider: ModuleConfigProvider) {
            super();
            this.moduleConfigProvider = moduleConfigProvider;
        }

        createSelectedOption(option: Option<Module>): SelectedOption<Module> {
            var moduleConfig = this.moduleConfigProvider.getConfig(option.displayValue.getModuleKey());
            var moduleConfigData = moduleConfig ? moduleConfig.getConfig() : new RootDataSet();
            var optionView = new ModuleConfiguratorSelectedOptionView(option, moduleConfigData);

            return new SelectedOption<Module>(optionView, this.count());
        }

    }

    export class ModuleConfiguratorSelectedOptionView extends ModuleView implements SelectedOptionView<Module> {

        private option: Option<Module>;

        private selectedOptionToBeRemovedListeners: {(): void;}[];

        constructor(option: Option<Module>, config: RootDataSet) {
            this.selectedOptionToBeRemovedListeners = [];
            this.option = option;

            super(option.displayValue, config);

            this.onRemoveClicked((event: MouseEvent) => {
                this.notifySelectedOptionRemoveRequested();
            })

        }

        setOption(option: Option<Module>) {
            this.option = option;
        }

        getOption(): Option<Module> {
            return this.option;
        }

        notifySelectedOptionRemoveRequested() {
            this.selectedOptionToBeRemovedListeners.forEach((listener) => {
                listener();
            });
        }

        onSelectedOptionRemoveRequest(listener: {(): void;}) {
            this.selectedOptionToBeRemovedListeners.push(listener);
        }

        unSelectedOptionRemoveRequest(listener: {(): void;}) {
            this.selectedOptionToBeRemovedListeners = this.selectedOptionToBeRemovedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

    }
}