module app.installation {

    import ApplicationKey = api.application.ApplicationKey;
    import UploadItem = api.ui.uploader.UploadItem;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import ApplicationUploaderEl = api.application.ApplicationUploaderEl;
    import Application = api.application.Application;

    import DockedPanel = api.ui.panel.DockedPanel;

    import MarketAppPanel = app.installation.view.MarketAppPanel;
    import UploadAppPanel = app.installation.view.UploadAppPanel;

    export class InstallAppDialog extends api.ui.dialog.ModalDialog {

        private installAppDialogTitle: api.ui.dialog.ModalDialogHeader;

        private installAppDockedPanel: DockedPanel;

        private uploadAppPanel: UploadAppPanel;

        private marketAppPanel: MarketAppPanel;

        constructor() {

            this.installAppDialogTitle = new api.ui.dialog.ModalDialogHeader("Install Application");

            super({
                title: this.installAppDialogTitle
            });

            this.addClass("install-application-dialog hidden");

            this.initUploadAppPanel();

            this.initMarketAppPanel();

            this.initAndAppendInstallAppsTabsPanel();

            api.dom.Body.get().appendChild(this);
        }

        private initMarketAppPanel() {
            this.marketAppPanel = new MarketAppPanel("market-app-panel");
        }

        private initUploadAppPanel() {

            this.uploadAppPanel = new UploadAppPanel(this.getCancelAction(), "upload-app-panel");

            this.uploadAppPanel.getApplicationInput().onKeyUp((event: KeyboardEvent) => {
                if (event.keyCode === 27) {
                    this.getCancelAction().execute();
                }
            });

            this.uploadAppPanel.getApplicationInput().onUploadStarted((event: FileUploadStartedEvent<Application>) => {
                this.closeAndFireEventFromMediaUpload(event.getUploadItems());
            });

            this.uploadAppPanel.getApplicationUploaderEl().onUploadStarted((event: FileUploadStartedEvent<Application>) => {
                this.closeAndFireEventFromMediaUpload(event.getUploadItems());
            });
        }

        private initAndAppendInstallAppsTabsPanel() {
            this.installAppDockedPanel = new DockedPanel();
            this.installAppDockedPanel.addClass("install-app-docked-panel");
            this.installAppDockedPanel.addItem("Upload", true, this.uploadAppPanel);
            this.installAppDockedPanel.addItem("Enonic Market", true, this.marketAppPanel);

            this.appendChildToContentPanel(this.installAppDockedPanel);
        }

        open() {
            super.open();
        }

        show() {
            this.resetFileInputWithUploader();
            super.show();
            this.uploadAppPanel.getApplicationInput().giveFocus();
        }

        hide() {
            super.hide();
            this.uploadAppPanel.getApplicationUploaderEl().stop();
            this.addClass("hidden");
            this.removeClass("animated");
        }

        close() {
            this.uploadAppPanel.getApplicationInput().reset();
            super.close();
        }

        private resetFileInputWithUploader() {
            this.uploadAppPanel.getApplicationUploaderEl().reset();
            this.uploadAppPanel.getApplicationInput().reset();
        }

        private closeAndFireEventFromMediaUpload(items: UploadItem<Application>[]) {
            this.close();
            new api.application.ApplicationUploadStartedEvent(items).fire();
        }
    }
}
