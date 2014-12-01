module api.content.site.inputtype.moduleconfigurator {

    import Property = api.data2.Property;
    import PropertyTree = api.data2.PropertyTree;
    import PropertySet = api.data2.PropertySet;
    import Module = api.module.Module;
    import ModuleViewer = api.module.ModuleViewer;
    import ModuleLoader = api.module.ModuleLoader;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import SelectedOptionView = api.ui.selector.combobox.SelectedOptionView;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;


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

        getSelectedOptionViews(): ModuleConfiguratorSelectedOptionView[] {
            var views: ModuleConfiguratorSelectedOptionView[] = [];
            this.getSelectedOptions().forEach((selectedOption: SelectedOption<Module>) => {
                views.push(<ModuleConfiguratorSelectedOptionView>selectedOption.getOptionView());
            });
            return views;
        }

    }

    export class ModuleConfiguratorSelectedOptionsView extends BaseSelectedOptionsView<Module> {

        private moduleConfigProvider: ModuleConfigProvider;

        constructor(moduleConfigProvider: ModuleConfigProvider) {
            super();
            this.moduleConfigProvider = moduleConfigProvider;
        }

        createSelectedOption(option: Option<Module>): SelectedOption<Module> {
            var moduleConfig = this.moduleConfigProvider.getConfig(option.displayValue.getModuleKey());
            var moduleConfigData: PropertySet = moduleConfig ? moduleConfig.getConfig() : new PropertyTree().getRoot();
            var optionView = new ModuleConfiguratorSelectedOptionView(option, moduleConfigData);

            return new SelectedOption<Module>(optionView, this.count());
        }

    }

    export class ModuleConfiguratorSelectedOptionView extends ModuleView implements SelectedOptionView<Module> {

        private option: Option<Module>;

        private selectedOptionToBeRemovedListeners: {(): void;}[];

        constructor(option: Option<Module>, config: PropertySet) {
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