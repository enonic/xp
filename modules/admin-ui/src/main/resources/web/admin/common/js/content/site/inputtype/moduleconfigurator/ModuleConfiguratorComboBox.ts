module api.content.site.inputtype.moduleconfigurator {

    import Property = api.data.Property;
    import PropertyTree = api.data.PropertyTree;
    import PropertySet = api.data.PropertySet;
    import Module = api.module.Module;
    import ModuleKey = api.module.ModuleKey;
    import ModuleViewer = api.module.ModuleViewer;
    import ModuleLoader = api.module.ModuleLoader;
    import FormView = api.form.FormView;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import SelectedOptionView = api.ui.selector.combobox.SelectedOptionView;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;

    export class ModuleConfiguratorComboBox extends api.ui.selector.combobox.RichComboBox<Module> {

        private moduleConfiguratorSelectedOptionsView: ModuleConfiguratorSelectedOptionsView;

        constructor(maxOccurrences: number, moduleConfigProvider: ModuleConfigProvider, formContext: api.content.form.ContentFormContext) {

            this.moduleConfiguratorSelectedOptionsView = new ModuleConfiguratorSelectedOptionsView(moduleConfigProvider, formContext);
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<Module>();
            builder.
                setMaximumOccurrences(maxOccurrences).
                setIdentifierMethod('getModuleKey').
                setComboBoxName("moduleSelector").
                setLoader(new ModuleLoader()).
                setSelectedOptionsView(this.moduleConfiguratorSelectedOptionsView).
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

        onModuleConfigFormDisplayed(listener: {(moduleKey: ModuleKey, formView: FormView): void;}) {
            this.moduleConfiguratorSelectedOptionsView.onModuleConfigFormDisplayed(listener);
        }

        unModuleConfigFormDisplayed(listener: {(moduleKey: ModuleKey, formView: FormView): void;}) {
            this.moduleConfiguratorSelectedOptionsView.unModuleConfigFormDisplayed(listener);
        }
    }

    export class ModuleConfiguratorSelectedOptionsView extends BaseSelectedOptionsView<Module> {

        private moduleConfigProvider: ModuleConfigProvider;

        private moduleConfigFormDisplayedListeners: {(moduleKey: ModuleKey, formView: FormView) : void}[] = [];

        private formContext: api.content.form.ContentFormContext;

        constructor(moduleConfigProvider: ModuleConfigProvider, formContext: api.content.form.ContentFormContext) {
            super();
            this.moduleConfigProvider = moduleConfigProvider;
            this.formContext = formContext;
        }

        createSelectedOption(option: Option<Module>): SelectedOption<Module> {
            var moduleConfig = this.moduleConfigProvider.getConfig(option.displayValue.getModuleKey());
            var moduleConfigData: PropertySet = moduleConfig ? moduleConfig.getConfig() : new PropertyTree().getRoot();
            var optionView = new ModuleConfiguratorSelectedOptionView(option, moduleConfigData, this.formContext);
            optionView.onModuleConfigFormDisplayed((moduleKey: ModuleKey) => {
                this.notifyModuleConfigFormDisplayed(moduleKey, optionView.getFormView());
            });

            return new SelectedOption<Module>(optionView, this.count());
        }

        onModuleConfigFormDisplayed(listener: {(moduleKey: ModuleKey, formView: FormView): void;}) {
            this.moduleConfigFormDisplayedListeners.push(listener);
        }

        unModuleConfigFormDisplayed(listener: {(moduleKey: ModuleKey, formView: FormView): void;}) {
            this.moduleConfigFormDisplayedListeners =
            this.moduleConfigFormDisplayedListeners.filter((curr) => (curr != listener));
        }

        private notifyModuleConfigFormDisplayed(moduleKey: ModuleKey, formView: FormView) {
            this.moduleConfigFormDisplayedListeners.forEach((listener) => listener(moduleKey, formView));
        }

    }

    export class ModuleConfiguratorSelectedOptionView extends ModuleView implements SelectedOptionView<Module> {

        private option: Option<Module>;

        private selectedOptionToBeRemovedListeners: {(): void;}[];

        constructor(option: Option<Module>, config: PropertySet, formContext: api.content.form.ContentFormContext) {
            this.selectedOptionToBeRemovedListeners = [];
            this.option = option;

            super(option.displayValue, config, formContext);

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