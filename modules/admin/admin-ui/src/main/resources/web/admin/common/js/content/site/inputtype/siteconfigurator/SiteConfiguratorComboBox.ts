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


    export class SiteConfiguratorComboBox extends api.ui.selector.combobox.RichComboBox<Application> {

        private siteConfiguratorSelectedOptionsView: SiteConfiguratorSelectedOptionsView;

        constructor(maxOccurrences: number, siteConfigProvider: SiteConfigProvider,
                    formContext: api.content.form.ContentFormContext, value?: string) {

            var filterObject = {
                state: Application.STATE_STARTED
            };

            this.siteConfiguratorSelectedOptionsView = new SiteConfiguratorSelectedOptionsView(siteConfigProvider, formContext);
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<Application>();
            builder.
                setMaximumOccurrences(maxOccurrences).
                setIdentifierMethod('getApplicationKey').
                setComboBoxName("applicationSelector").
                setLoader(new ApplicationLoader(500, filterObject)).
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

    }

}