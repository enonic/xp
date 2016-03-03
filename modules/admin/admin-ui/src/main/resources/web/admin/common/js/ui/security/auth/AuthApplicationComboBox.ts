module api.ui.security.auth {

    export class AuthApplicationComboBox extends api.ui.selector.combobox.RichComboBox<api.application.Application> {
        constructor() {
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<api.application.Application>();
            builder.
                setMaximumOccurrences(1).
                setComboBoxName("authApplicationSelector").
                setLoader(new api.security.auth.AuthApplicationLoader()).
                setSelectedOptionsView(new AuthApplicationSelectedOptionsView()).
                setOptionDisplayValueViewer(new AuthApplicationViewer()).
                setDelayedInputValueChangedHandling(500);
            super(builder);
        }
    }

    export class AuthApplicationSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<api.application.Application> {

        createSelectedOption(option: api.ui.selector.Option<api.application.Application>): api.ui.selector.combobox.SelectedOption<api.application.Application> {
            var optionView = new AuthApplicationSelectedOptionView(option);
            return new api.ui.selector.combobox.SelectedOption<api.application.Application>(optionView, this.count());
        }
    }

    export class AuthApplicationSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<api.application.Application> {

        private application: api.application.Application;

        private authConfig: api.security.AuthConfig;

        private formView;


        constructor(option: api.ui.selector.Option<api.application.Application>) {
            super(option);
            this.application = option.displayValue;
            this.authConfig = api.security.AuthConfig.create().
                setConfig(new api.data.PropertyTree(undefined)).
                setApplicationKey(this.application.getApplicationKey()).
                build();
        }

        getAuthConfig(): api.security.AuthConfig {
            return this.authConfig;
        }

        setAuthConfig(authConfig: api.security.AuthConfig) {
            this.authConfig = authConfig;
        }

        resolveIconUrl(content: api.application.Application): string {
            return api.util.UriHelper.getAdminUri("common/images/icons/icoMoon/32x32/puzzle.png");
        }

        resolveTitle(content: api.application.Application): string {
            return content.getDisplayName();
        }

        resolveSubTitle(content: api.application.Application): string {
            return content.getApplicationKey().toString();
        }

        createActionButtons(content: api.application.Application): api.dom.Element[] {
            if (content.getAuthForm().getFormItems().length > 0) {
                let editButton = new api.dom.AEl("edit");
                editButton.onClicked((event: MouseEvent) => {
                    this.initAndOpenConfigureDialog();
                });
                return [editButton];
            }
            return [];
        }

        initAndOpenConfigureDialog() {
            if (this.application.getAuthForm().getFormItems().length > 0) {

                var tempSiteConfig: api.security.AuthConfig = this.makeTemporaryAuthConfig();
                var formViewStateOnDialogOpen = this.formView;
                this.formView = this.createFormView(tempSiteConfig);

                var okCallback = () => {
                    if (!tempSiteConfig.equals(this.authConfig)) {
                        this.applyTemporaryConfig(tempSiteConfig); //TODO Save on apply?
                    }
                };

                var siteConfiguratorDialog = new api.content.site.inputtype.siteconfigurator.SiteConfiguratorDialog(this.application.getDisplayName(),
                    this.application.getName() + "-" + this.application.getVersion(),
                    this.formView,
                    okCallback,
                    () => {
                    });
                siteConfiguratorDialog.open();
            }
        }

        private makeTemporaryAuthConfig(): api.security.AuthConfig {
            return api.security.AuthConfig.create().
                setConfig(this.authConfig.getConfig().copy()).
                setApplicationKey(this.authConfig.getApplicationKey()).build();
        }

        private createFormView(authConfig: api.security.AuthConfig): api.form.FormView {
            var formView = new api.form.FormView(api.form.FormContext.create().build(), this.application.getAuthForm(),
                authConfig.getConfig().getRoot());
            formView.addClass("site-form");
            formView.layout().then(() => {
                this.formView.validate(false, true);
                this.toggleClass("invalid", !this.formView.isValid());
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();

            return formView;
        }

        private applyTemporaryConfig(tempSiteConfig: api.security.AuthConfig) {
            tempSiteConfig.getConfig().getRoot().forEach((property) => {
                this.authConfig.getConfig().setProperty(property.getName(), property.getIndex(), property.getValue());
            });
            this.authConfig.getConfig().getRoot().forEach((property) => {
                var prop = tempSiteConfig.getConfig().getProperty(property.getName(), property.getIndex());
                if (!prop) {
                    this.authConfig.getConfig().removeProperty(property.getName(), property.getIndex());
                }
            });
        }

    }
}