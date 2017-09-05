module api.content.site.inputtype.siteconfigurator {

    import PropertyTree = api.data.PropertyTree;
    import PropertySet = api.data.PropertySet;
    import Option = api.ui.selector.Option;
    import FormView = api.form.FormView;
    import FormContextBuilder = api.form.FormContextBuilder;
    import Application = api.application.Application;
    import ApplicationKey = api.application.ApplicationKey;
    import SiteConfig = api.content.site.SiteConfig;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import ContentFormContext = api.content.form.ContentFormContext;

    export class SiteConfiguratorSelectedOptionView extends api.ui.selector.combobox.BaseSelectedOptionView<Application> {

        private application: Application;

        private formView: FormView;

        private siteConfig: SiteConfig;

        private editClickedListeners: {(event: MouseEvent): void;}[];

        private siteConfigFormDisplayedListeners: {(applicationKey: ApplicationKey): void}[];

        private formContext: ContentFormContext;

        private formValidityChangedHandler: {(event: api.form.FormValidityChangedEvent): void};

        private configureDialog: SiteConfiguratorDialog;

        private formViewStateOnDialogOpen: FormView;

        constructor(option: Option<Application>, siteConfig: SiteConfig, formContext: api.content.form.ContentFormContext) {
            super(option);

            this.editClickedListeners = [];
            this.siteConfigFormDisplayedListeners = [];

            this.application = option.displayValue;
            this.siteConfig = siteConfig;
            this.formContext = formContext;
        }

        doRender(): wemQ.Promise<boolean> {

            let header = new api.dom.DivEl('header');

            let namesAndIconView = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().setSize(
                api.app.NamesAndIconViewSize.small)).setMainName(this.application.getDisplayName()).setSubName(
                this.application.getName() + (!!this.application.getVersion() ? '-' + this.application.getVersion() : ''));

            if (this.application.getIconUrl()) {
                namesAndIconView.setIconUrl(this.application.getIconUrl());
            }

            if (this.application.getDescription()) {
                namesAndIconView.setSubName(this.application.getDescription());
            }

            header.appendChild(namesAndIconView);

            this.appendChild(header);

            this.formValidityChangedHandler = (event: api.form.FormValidityChangedEvent) => {
                this.toggleClass('invalid', !event.isValid());
            };

            this.toggleClass('empty', !!this.getOption().empty);
            this.toggleClass('stopped', this.application.getState() === Application.STATE_STOPPED);

            this.formView = this.createFormView(this.siteConfig);

            if (this.application.getForm() && this.application.getForm().getFormItems().length > 0) {
                header.appendChild(this.createEditButton());
            }

            let removeButton = new api.dom.AEl('remove');
            removeButton.onClicked((event: MouseEvent) => {
                if (this.isEditable()) {
                    this.notifyRemoveClicked();
                    event.stopPropagation();
                    event.preventDefault();
                    return false;
                }
            });
            header.appendChild(removeButton);

            this.configureDialog = this.initConfigureDialog();
            if(this.configureDialog) {
                return this.configureDialog.render(true)
                    .then(()=> wemQ(true));
            } else {
                return wemQ(true);
            }
        }

        setSiteConfig(siteConfig: SiteConfig) {
            this.siteConfig = siteConfig;
        }

        private createEditButton(): api.dom.AEl {
            let editButton = new api.dom.AEl('edit');

            editButton.onClicked((event: MouseEvent) => {
                if (this.isEditable()) {
                    this.notifyEditClicked(event);
                    this.showConfigureDialog();
                    event.stopPropagation();
                    event.preventDefault();
                    return false;
                }
            });

            return editButton;
        }
        showConfigureDialog() {

            if (this.formView) {
                this.formViewStateOnDialogOpen = this.formView;
                this.unbindValidationEvent(this.formViewStateOnDialogOpen);
            }

            this.configureDialog = this.initConfigureDialog();
            if (this.configureDialog) {
                this.configureDialog.open();
            }
        }

        initConfigureDialog(): SiteConfiguratorDialog {
            if (!this.application.getForm().getFormItems().length) {
                return null;
            }

            let tempSiteConfig: SiteConfig = this.makeTemporarySiteConfig();

            this.formView = this.createFormView(tempSiteConfig);
            this.bindValidationEvent(this.formView);

            let okCallback = () => {
                if (!tempSiteConfig.equals(this.siteConfig)) {
                    this.applyTemporaryConfig(tempSiteConfig);
                    new api.content.event.ContentRequiresSaveEvent(this.formContext.getPersistedContent().getContentId()).fire();
                }
            };

            let cancelCallback = () => {
                this.revertFormViewToGivenState(this.formViewStateOnDialogOpen);
            };

            let siteConfiguratorDialog = new SiteConfiguratorDialog(this.application,
                this.formView,
                okCallback,
                cancelCallback);

            return siteConfiguratorDialog;
        }

        private revertFormViewToGivenState(formViewStateToRevertTo: FormView) {
            this.unbindValidationEvent(this.formView);
            this.formView = formViewStateToRevertTo;
            this.formView.validate(false, true);
            this.toggleClass('invalid', !this.formView.isValid());
        }

        private undoSelectionOnCancel(comboBoxToUndoSelectionOnCancel: SiteConfiguratorComboBox) {
            comboBoxToUndoSelectionOnCancel.deselect(this.application);
        }

        private applyTemporaryConfig(tempSiteConfig: SiteConfig) {
            tempSiteConfig.getConfig().forEach((property) => {
                this.siteConfig.getConfig().setProperty(property.getName(), property.getIndex(), property.getValue());
            });
            this.siteConfig.getConfig().forEach((property) => {
                let prop = tempSiteConfig.getConfig().getProperty(property.getName(), property.getIndex());
                if (!prop) {
                    this.siteConfig.getConfig().removeProperty(property.getName(), property.getIndex());
                }
            });
        }

        private makeTemporarySiteConfig(): SiteConfig {
            let propSet = (new PropertyTree(this.siteConfig.getConfig())).getRoot();
            propSet.setContainerProperty(this.siteConfig.getConfig().getProperty());
            return SiteConfig.create().setConfig(propSet).setApplicationKey(this.siteConfig.getApplicationKey()).build();
        }

        private createFormView(siteConfig: SiteConfig): FormView {
            let formView = new FormView(this.formContext, this.application.getForm(), siteConfig.getConfig());
            formView.addClass('site-form');

            formView.onLayoutFinished(() => {
                formView.displayValidationErrors(true);
                formView.validate(false, true);
                this.toggleClass('invalid', !formView.isValid());
                this.notifySiteConfigFormDisplayed(this.application.getApplicationKey());
            });

            return formView;
        }

        private bindValidationEvent(formView: FormView) {
            if (formView) {
                formView.onValidityChanged(this.formValidityChangedHandler);
            }
        }

        private unbindValidationEvent(formView: FormView) {
            if (formView) {
                formView.unValidityChanged(this.formValidityChangedHandler);
            }
        }

        getApplication(): Application {
            return this.application;
        }

        getSiteConfig(): SiteConfig {
            return this.siteConfig;
        }

        getFormView(): FormView {
            return this.formView;
        }

        onEditClicked(listener: (event: MouseEvent) => void) {
            this.editClickedListeners.push(listener);
        }

        unEditClicked(listener: (event: MouseEvent) => void) {
            this.editClickedListeners = this.editClickedListeners.filter((curr) => {
                return listener !== curr;
            });
        }

        private notifyEditClicked(event: MouseEvent) {
            this.editClickedListeners.forEach((listener) => {
                listener(event);
            });
        }

        onSiteConfigFormDisplayed(listener: {(applicationKey: ApplicationKey): void;}) {
            this.siteConfigFormDisplayedListeners.push(listener);
        }

        unSiteConfigFormDisplayed(listener: {(applicationKey: ApplicationKey): void;}) {
            this.siteConfigFormDisplayedListeners =
                this.siteConfigFormDisplayedListeners.filter((curr) => (curr !== listener));
        }

        private notifySiteConfigFormDisplayed(applicationKey: ApplicationKey) {
            this.siteConfigFormDisplayedListeners.forEach((listener) => listener(applicationKey));
        }
    }
}
