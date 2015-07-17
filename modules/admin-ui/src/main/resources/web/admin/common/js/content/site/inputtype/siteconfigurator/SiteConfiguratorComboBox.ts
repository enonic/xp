module api.content.site.inputtype.siteconfigurator {

    import Property = api.data.Property;
    import PropertyTree = api.data.PropertyTree;
    import PropertySet = api.data.PropertySet;
    import Module = api.module.Module;
    import ApplicationKey = api.module.ApplicationKey;
    import SiteConfig = api.content.site.SiteConfig;
    import ModuleViewer = api.module.ModuleViewer;
    import ModuleLoader = api.module.ModuleLoader;
    import FormView = api.form.FormView;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import SelectedOptionView = api.ui.selector.combobox.SelectedOptionView;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;

    export class SiteConfiguratorComboBox extends api.ui.selector.combobox.RichComboBox<Module> {

        private siteConfiguratorSelectedOptionsView: SiteConfiguratorSelectedOptionsView;

        constructor(maxOccurrences: number, siteConfigProvider: SiteConfigProvider, formContext: api.content.form.ContentFormContext) {
            var filterObject = {
                state: Module.STATE_STARTED
            };

            this.siteConfiguratorSelectedOptionsView = new SiteConfiguratorSelectedOptionsView(siteConfigProvider, formContext);
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<Module>();
            builder.
                setMaximumOccurrences(maxOccurrences).
                setIdentifierMethod('getApplicationKey').
                setComboBoxName("moduleSelector").
                setLoader(new ModuleLoader(500, filterObject)).
                setSelectedOptionsView(this.siteConfiguratorSelectedOptionsView).
                setOptionDisplayValueViewer(new ModuleViewer()).
                setDelayedInputValueChangedHandling(500);

            super(builder);
        }

        getSelectedOptionViews(): SiteConfiguratorSelectedOptionView[] {
            var views: SiteConfiguratorSelectedOptionView[] = [];
            this.getSelectedOptions().forEach((selectedOption: SelectedOption<Module>) => {
                views.push(<SiteConfiguratorSelectedOptionView>selectedOption.getOptionView());
            });
            return views;
        }

        onSiteConfigFormDisplayed(listener: {(applicationKey: ApplicationKey, formView: FormView): void;}) {
            this.siteConfiguratorSelectedOptionsView.onSiteConfigFormDisplayed(listener);
        }

        unSiteConfigFormDisplayed(listener: {(applicationKey: ApplicationKey, formView: FormView): void;}) {
            this.siteConfiguratorSelectedOptionsView.unSiteConfigFormDisplayed(listener);
        }
    }

    export class SiteConfiguratorSelectedOptionsView extends BaseSelectedOptionsView<Module> {

        private siteConfigProvider: SiteConfigProvider;

        private siteConfigFormDisplayedListeners: {(applicationKey: ApplicationKey, formView: FormView) : void}[] = [];

        private formContext: api.content.form.ContentFormContext;

        constructor(siteConfigProvider: SiteConfigProvider, formContext: api.content.form.ContentFormContext) {
            super();
            this.siteConfigProvider = siteConfigProvider;
            this.formContext = formContext;
        }

        createSelectedOption(option: Option<Module>): SelectedOption<Module> {
            var siteConfig = this.siteConfigProvider.getConfig(option.displayValue.getApplicationKey());
            var optionView = new SiteConfiguratorSelectedOptionView(option, siteConfig, this.formContext);
            optionView.onSiteConfigFormDisplayed((applicationKey: ApplicationKey) => {
                this.notifySiteConfigFormDisplayed(applicationKey, optionView.getFormView());
            });

            return new SelectedOption<Module>(optionView, this.count());
        }

        onSiteConfigFormDisplayed(listener: {(applicationKey: ApplicationKey, formView: FormView): void;}) {
            this.siteConfigFormDisplayedListeners.push(listener);
        }

        unSiteConfigFormDisplayed(listener: {(applicationKey: ApplicationKey, formView: FormView): void;}) {
            this.siteConfigFormDisplayedListeners =
            this.siteConfigFormDisplayedListeners.filter((curr) => (curr != listener));
        }

        private notifySiteConfigFormDisplayed(applicationKey: ApplicationKey, formView: FormView) {
            this.siteConfigFormDisplayedListeners.forEach((listener) => listener(applicationKey, formView));
        }

    }

    export class SiteConfiguratorSelectedOptionView extends SiteView implements SelectedOptionView<Module> {

        private option: Option<Module>;

        private selectedOptionToBeRemovedListeners: {(): void;}[];

        constructor(option: Option<Module>, siteConfig: SiteConfig, formContext: api.content.form.ContentFormContext) {
            this.selectedOptionToBeRemovedListeners = [];
            this.option = option;

            super(option.displayValue, siteConfig, formContext);

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