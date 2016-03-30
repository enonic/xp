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

        private uploadAppPanel: UploadAppPanel;

        private marketAppPanel: MarketAppPanel;

        private onMarketLoaded;

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Install Application")
            });

            this.addClass("install-application-dialog hidden");

            this.onMarketLoaded = this.centerMyself.bind(this);

            this.initUploadAppPanel();

            this.initMarketAppPanel();

            this.initAndAppendInstallAppsTabsPanel();

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
            let installAppDockedPanel = new DockedPanel();
            installAppDockedPanel.addClass("install-app-docked-panel");
            installAppDockedPanel.addItem("Enonic Market", true, this.marketAppPanel);
            installAppDockedPanel.addItem("Upload", true, this.uploadAppPanel);

            this.appendChildToContentPanel(installAppDockedPanel);
        }

        private initUploaderListeners() {

            let uploadFailedHandler = (event: FileUploadFailedEvent<Application>, uploader: ApplicationUploaderEl) => {
                this.uploadAppPanel.getApplicationInput().showFailure(
                    uploader.getFailure());
                this.resetFileInputWithUploader();
            };

            this.uploadAppPanel.getApplicationInput().onUploadFailed((event) => {
                uploadFailedHandler(event, this.uploadAppPanel.getApplicationInput().getUploader())
            });

            this.uploadAppPanel.getApplicationUploaderEl().onUploadFailed((event) => {
                uploadFailedHandler(event, this.uploadAppPanel.getApplicationUploaderEl())
            });

            let uploadCompletedHandler = (event: FileUploadCompleteEvent<Application>) => {
                if (event.getUploadItems()) {
                    this.close();
                }
            };

            this.uploadAppPanel.getApplicationInput().onUploadCompleted(uploadCompletedHandler);
            this.uploadAppPanel.getApplicationUploaderEl().onUploadCompleted(uploadCompletedHandler);

            let uploadStartedHandler = (event: FileUploadStartedEvent<Application>) => {
                new api.application.ApplicationUploadStartedEvent(event.getUploadItems()).fire();
            };

            this.uploadAppPanel.getApplicationInput().onUploadStarted(uploadStartedHandler);
            this.uploadAppPanel.getApplicationUploaderEl().onUploadStarted(uploadStartedHandler);
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
