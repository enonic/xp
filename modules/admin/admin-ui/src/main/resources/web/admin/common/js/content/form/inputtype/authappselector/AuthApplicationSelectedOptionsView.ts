module api.content.site.inputtype.authappselector {

    import Application = api.application.Application;
    import ApplicationKey = api.application.ApplicationKey;

    import FormView = api.form.FormView;

    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import SelectedOptionView = api.ui.selector.combobox.SelectedOptionView;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;
    import SiteConfigProvider = api.content.site.inputtype.siteconfigurator.SiteConfigProvider;

    export class AuthApplicationSelectedOptionsView extends BaseSelectedOptionsView<Application> {

        private siteConfigProvider: SiteConfigProvider;

        private siteConfigFormDisplayedListeners: {(applicationKey: ApplicationKey, formView: FormView) : void}[] = [];

        private beforeOptionCreatedListeners: {():void}[] = [];

        private afterOptionCreatedListeners: {():void}[] = [];

        private formContext: api.content.form.ContentFormContext;

        private items: AuthApplicationSelectedOptionView[] = [];

        private readOnly: boolean;

        constructor(siteConfigProvider: SiteConfigProvider, formContext: api.content.form.ContentFormContext, readOnly: boolean) {
            super();
            this.readOnly = readOnly;
            this.siteConfigProvider = siteConfigProvider;
            this.formContext = formContext;

            this.siteConfigProvider.onPropertyChanged(() => {

                this.items.forEach((optionView) => {
                    let newConfig = this.siteConfigProvider.getConfig(optionView.getSiteConfig().getApplicationKey(), false);
                    if (newConfig) {
                        optionView.setSiteConfig(newConfig);
                    }
                });

            });

            this.setOccurrencesSortable(true);
        }

        createSelectedOption(option: Option<Application>): SelectedOption<Application> {
            this.notifyBeforeOptionCreated();

            let siteConfig = this.siteConfigProvider.getConfig(option.displayValue.getApplicationKey());
            let optionView = new AuthApplicationSelectedOptionView(option, siteConfig, this.formContext, this.readOnly);

            optionView.onSiteConfigFormDisplayed((applicationKey: ApplicationKey) => {
                this.notifySiteConfigFormDisplayed(applicationKey, optionView.getFormView());
            });
            this.items.push(optionView);

            this.notifyAfterOptionCreated();
            return new SelectedOption<Application>(optionView, this.count());
        }

        removeOption(optionToRemove: api.ui.selector.Option<Application>, silent: boolean = false) {
            this.items = this.items.filter(item => !item.getSiteConfig().getApplicationKey().
                equals(optionToRemove.displayValue.getApplicationKey()));
            super.removeOption(optionToRemove, silent);
        }

        onSiteConfigFormDisplayed(listener: {(applicationKey: ApplicationKey, formView: FormView): void;}) {
            this.siteConfigFormDisplayedListeners.push(listener);
        }

        unSiteConfigFormDisplayed(listener: {(applicationKey: ApplicationKey, formView: FormView): void;}) {
            this.siteConfigFormDisplayedListeners =
                this.siteConfigFormDisplayedListeners.filter((curr) => (curr !== listener));
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
