module app.installation {

    import ApplicationKey = api.application.ApplicationKey;
    import UploadItem = api.ui.uploader.UploadItem;
    import FileUploadCompleteEvent = api.ui.uploader.FileUploadCompleteEvent;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import FileUploadFailedEvent = api.ui.uploader.FileUploadFailedEvent;
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

        private onMarketLoaded;

        constructor() {

            this.installAppDialogTitle = new api.ui.dialog.ModalDialogHeader("Install Application");

            super({
                title: this.installAppDialogTitle
            });

            this.addClass("install-application-dialog hidden");

            this.initUploadAppPanel();

            this.initMarketAppPanel();

            this.initAndAppendInstallAppsTabsPanel();

            this.onMarketLoaded = this.centerMyself.bind(this);

            api.dom.Body.get().appendChild(this);
        }

        private initMarketAppPanel() {
            this.marketAppPanel = new MarketAppPanel("market-app-panel");
            this.marketAppPanel.onShown(() => {
                this.marketAppPanel.getMarketAppsTreeGrid().onLoaded(this.onMarketLoaded)
            });
        }

        private initUploadAppPanel() {

            this.uploadAppPanel = new UploadAppPanel(this.getCancelAction(), "upload-app-panel");

            this.uploadAppPanel.getApplicationInput().onKeyUp((event: KeyboardEvent) => {
                if (event.keyCode === 27) {
                    this.getCancelAction().execute();
                }
            });

            this.initUploaderListeners();

        }

        private initAndAppendInstallAppsTabsPanel() {
            this.installAppDockedPanel = new DockedPanel();
            this.installAppDockedPanel.addClass("install-app-docked-panel");
            this.installAppDockedPanel.addItem("Upload", true, this.uploadAppPanel);
            this.installAppDockedPanel.addItem("Enonic Market", true, this.marketAppPanel);

            this.appendChildToContentPanel(this.installAppDockedPanel);
        }

        private initUploaderListeners() {
            this.uploadAppPanel.getApplicationInput().onUploadFailed((event: FileUploadFailedEvent<Application>) => {
                this.uploadAppPanel.getApplicationInput().showFailure(
                    this.uploadAppPanel.getApplicationInput().getUploader().getFailure());
                this.uploadAppPanel.getApplicationInput().reset();
            });

            this.uploadAppPanel.getApplicationUploaderEl().onUploadFailed((event: FileUploadFailedEvent<Application>) => {
                this.uploadAppPanel.getApplicationInput().showFailure(
                    this.uploadAppPanel.getApplicationUploaderEl().getFailure());
                this.uploadAppPanel.getApplicationUploaderEl().reset();
            });

            this.uploadAppPanel.getApplicationInput().onUploadCompleted((event: FileUploadCompleteEvent<Application>) => {
                if(event.getUploadItems()) {
                    this.close();
                }
            });

            this.uploadAppPanel.getApplicationUploaderEl().onUploadCompleted((event: FileUploadCompleteEvent<Application>) => {
                if(event.getUploadItems()) {
                    this.close();
                }
            });

            this.uploadAppPanel.getApplicationInput().onUploadStarted((event: FileUploadStartedEvent<Application>) => {
                new api.application.ApplicationUploadStartedEvent(event.getUploadItems()).fire();
            });

            this.uploadAppPanel.getApplicationUploaderEl().onUploadStarted((event: FileUploadStartedEvent<Application>) => {
                new api.application.ApplicationUploadStartedEvent(event.getUploadItems()).fire();
            });
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
            if (!!this.marketAppPanel.getMarketAppsTreeGrid()) {
                this.marketAppPanel.getMarketAppsTreeGrid().unLoaded(this.onMarketLoaded);
            }
            this.uploadAppPanel.getApplicationInput().reset();
            super.close();
        }

        private resetFileInputWithUploader() {
            this.uploadAppPanel.getApplicationUploaderEl().reset();
            this.uploadAppPanel.getApplicationInput().reset();
        }
    }
}
