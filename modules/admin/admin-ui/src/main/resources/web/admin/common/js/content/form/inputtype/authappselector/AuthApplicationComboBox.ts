module api.content.site.inputtype.siteconfigurator {

    import Property = api.data.Property;
    import PropertyTree = api.data.PropertyTree;
    import Application = api.application.Application;
    import ApplicationKey = api.application.ApplicationKey;

    import ApplicationViewer = api.application.ApplicationViewer;
    import ApplicationLoader = api.application.ApplicationLoader;
    import FormView = api.form.FormView;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import SelectedOptionView = api.ui.selector.combobox.SelectedOptionView;

    export class AuthApplicationComboBox extends api.ui.selector.combobox.RichComboBox<Application> {

        private siteConfiguratorSelectedOptionsView: AuthApplicationSelectedOptionsView;

        constructor(maxOccurrences: number, siteConfigProvider: SiteConfigProvider,
                    formContext: api.content.form.ContentFormContext, value?: string) {

            this.siteConfiguratorSelectedOptionsView = new AuthApplicationSelectedOptionsView(siteConfigProvider, formContext);
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<Application>();
            builder.
                setMaximumOccurrences(maxOccurrences).
                setIdentifierMethod('getApplicationKey').
                setComboBoxName("applicationSelector").
                setLoader(new api.security.auth.AuthApplicationLoader()).
                setSelectedOptionsView(this.siteConfiguratorSelectedOptionsView).
                setOptionDisplayValueViewer(new ApplicationViewer()).
                setValue(value).
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

        onBeforeOptionCreated(listener: () => void) {
            this.siteConfiguratorSelectedOptionsView.onBeforeOptionCreated(listener);
        }

        unBeforeOptionCreated(listener: () => void) {
            this.siteConfiguratorSelectedOptionsView.unBeforeOptionCreated(listener);
        }

        onAfterOptionCreated(listener: () => void) {
            this.siteConfiguratorSelectedOptionsView.onAfterOptionCreated(listener);
        }

        unAfterOptionCreated(listener: () => void) {
            this.siteConfiguratorSelectedOptionsView.unAfterOptionCreated(listener);
        }
    }

}