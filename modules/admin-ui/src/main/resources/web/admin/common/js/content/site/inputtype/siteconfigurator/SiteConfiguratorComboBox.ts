module api.content.site.inputtype.siteconfigurator {

    import Property = api.data.Property;
    import PropertyTree = api.data.PropertyTree;
    import PropertySet = api.data.PropertySet;
    import Application = api.application.Application;
    import ApplicationKey = api.application.ApplicationKey;
    import SiteConfig = api.content.site.SiteConfig;
    import ModuleViewer = api.application.ApplicationViewer;
    import ModuleLoader = api.application.ApplicationLoader;
    import FormView = api.form.FormView;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import SelectedOptionView = api.ui.selector.combobox.SelectedOptionView;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;

    export class SiteConfiguratorComboBox extends api.ui.selector.combobox.RichComboBox<Application> {

        private siteConfiguratorSelectedOptionsView: SiteConfiguratorSelectedOptionsView;

        constructor(maxOccurrences: number, siteConfigProvider: SiteConfigProvider, formContext: api.content.form.ContentFormContext) {
            var filterObject = {
                state: Application.STATE_STARTED
            };

            this.siteConfiguratorSelectedOptionsView = new SiteConfiguratorSelectedOptionsView(siteConfigProvider, formContext);
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<Application>();
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
            this.getSelectedOptions().forEach((selectedOption: SelectedOption<Application>) => {
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

    export class SiteConfiguratorSelectedOptionsView extends BaseSelectedOptionsView<Application> {

        private siteConfigProvider: SiteConfigProvider;

        private siteConfigFormDisplayedListeners: {(applicationKey: ApplicationKey, formView: FormView) : void}[] = [];

        private formContext: api.content.form.ContentFormContext;

        constructor(siteConfigProvider: SiteConfigProvider, formContext: api.content.form.ContentFormContext) {
            super();
            this.siteConfigProvider = siteConfigProvider;
            this.formContext = formContext;
        }

        createSelectedOption(option: Option<Application>): SelectedOption<Application> {
            var siteConfig = this.siteConfigProvider.getConfig(option.displayValue.getApplicationKey());
            var optionView = new SiteConfiguratorSelectedOptionView(option, siteConfig, this.formContext);
            optionView.onSiteConfigFormDisplayed((applicationKey: ApplicationKey) => {
                this.notifySiteConfigFormDisplayed(applicationKey, optionView.getFormView());
            });

            return new SelectedOption<Application>(optionView, this.count());
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

    export class SiteConfiguratorSelectedOptionView extends SiteView implements SelectedOptionView<Application> {

        private option: Option<Application>;

        private selectedOptionToBeRemovedListeners: {(): void;}[];

        constructor(option: Option<Application>, siteConfig: SiteConfig, formContext: api.content.form.ContentFormContext) {
            this.selectedOptionToBeRemovedListeners = [];
            this.option = option;

            super(option.displayValue, siteConfig, formContext);

            this.onRemoveClicked((event: MouseEvent) => {
                this.notifySelectedOptionRemoveRequested();
            })

        }

        setOption(option: Option<Application>) {
            this.option = option;
        }

        getOption(): Option<Application> {
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