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

        private authApplicationSelectedOptionsView: AuthApplicationSelectedOptionsView;

        constructor(maxOccurrences: number, siteConfigProvider: SiteConfigProvider,
                    formContext: api.content.form.ContentFormContext, value?: string) {

            this.authApplicationSelectedOptionsView = new AuthApplicationSelectedOptionsView(siteConfigProvider, formContext);
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<Application>();
            builder.
                setMaximumOccurrences(maxOccurrences).
                setIdentifierMethod('getApplicationKey').
                setComboBoxName("applicationSelector").
                setLoader(new api.security.auth.AuthApplicationLoader()).
                setSelectedOptionsView(this.authApplicationSelectedOptionsView).
                setOptionDisplayValueViewer(new ApplicationViewer()).
                setValue(value).
                setDelayedInputValueChangedHandling(500);

            super(builder);
        }

        getSelectedOptionViews(): AuthApplicationSelectedOptionView[] {
            var views: AuthApplicationSelectedOptionView[] = [];
            this.getSelectedOptions().forEach((selectedOption: SelectedOption<Application>) => {
                views.push(<AuthApplicationSelectedOptionView>selectedOption.getOptionView());
            });
            return views;
        }

        onSiteConfigFormDisplayed(listener: {(applicationKey: ApplicationKey, formView: FormView): void;}) {
            this.authApplicationSelectedOptionsView.onSiteConfigFormDisplayed(listener);
        }

        unSiteConfigFormDisplayed(listener: {(applicationKey: ApplicationKey, formView: FormView): void;}) {
            this.authApplicationSelectedOptionsView.unSiteConfigFormDisplayed(listener);
        }

        onBeforeOptionCreated(listener: () => void) {
            this.authApplicationSelectedOptionsView.onBeforeOptionCreated(listener);
        }

        unBeforeOptionCreated(listener: () => void) {
            this.authApplicationSelectedOptionsView.unBeforeOptionCreated(listener);
        }

        onAfterOptionCreated(listener: () => void) {
            this.authApplicationSelectedOptionsView.onAfterOptionCreated(listener);
        }

        unAfterOptionCreated(listener: () => void) {
            this.authApplicationSelectedOptionsView.unAfterOptionCreated(listener);
        }
    }

}