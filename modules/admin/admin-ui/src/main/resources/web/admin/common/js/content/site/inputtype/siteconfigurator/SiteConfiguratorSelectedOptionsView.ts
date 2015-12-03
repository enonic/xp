module api.content.site.inputtype.siteconfigurator {

    import Application = api.application.Application;
    import ApplicationKey = api.application.ApplicationKey;

    import FormView = api.form.FormView;

    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import SelectedOptionView = api.ui.selector.combobox.SelectedOptionView;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;

    export class SiteConfiguratorSelectedOptionsView extends BaseSelectedOptionsView<Application> {

        private siteConfigProvider: SiteConfigProvider;

        private siteConfigFormDisplayedListeners: {(applicationKey: ApplicationKey, formView: FormView) : void}[] = [];

        private beforeOptionCreatedListeners: {():void}[] = [];

        private afterOptionCreatedListeners: {():void}[] = [];

        private formContext: api.content.form.ContentFormContext;

        constructor(siteConfigProvider: SiteConfigProvider, formContext: api.content.form.ContentFormContext) {
            super();
            this.siteConfigProvider = siteConfigProvider;
            this.formContext = formContext;

            this.setOccurrencesSortable(true);
        }

        createSelectedOption(option: Option<Application>): SelectedOption<Application> {
            this.notifyBeforeOptionCreated();

            var siteConfig = this.siteConfigProvider.getConfig(option.displayValue.getApplicationKey());
            var optionView = new SiteConfiguratorSelectedOptionView(option, siteConfig, this.formContext);

            optionView.onSiteConfigFormDisplayed((applicationKey: ApplicationKey) => {
                this.notifySiteConfigFormDisplayed(applicationKey, optionView.getFormView());
            });

            this.notifyAfterOptionCreated();
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


        onBeforeOptionCreated(listener: () => void) {
            this.beforeOptionCreatedListeners.push(listener);
        }

        unBeforeOptionCreated(listener: () => void) {
            this.beforeOptionCreatedListeners = this.beforeOptionCreatedListeners.filter((curr) => {
                return listener !== curr;
            });
        }

        private notifyBeforeOptionCreated() {
            this.beforeOptionCreatedListeners.forEach((listener) => listener());
        }

        onAfterOptionCreated(listener: () => void) {
            this.afterOptionCreatedListeners.push(listener);
        }

        unAfterOptionCreated(listener: () => void) {
            this.afterOptionCreatedListeners = this.afterOptionCreatedListeners.filter((curr) => {
                return listener !== curr;
            });
        }

        private notifyAfterOptionCreated() {
            this.afterOptionCreatedListeners.forEach((listener) => listener());
        }

    }
}