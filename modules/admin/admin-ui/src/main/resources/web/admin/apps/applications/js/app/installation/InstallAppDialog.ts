import "../../api.ts";
import {MarketAppPanel} from "./view/MarketAppPanel";
import {UploadAppPanel} from "./view/UploadAppPanel";

import ApplicationKey = api.application.ApplicationKey;
import UploadItem = api.ui.uploader.UploadItem;
import FileUploadCompleteEvent = api.ui.uploader.FileUploadCompleteEvent;
import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
import FileUploadFailedEvent = api.ui.uploader.FileUploadFailedEvent;
import ApplicationUploaderEl = api.application.ApplicationUploaderEl;
import Application = api.application.Application;

import DockedPanel = api.ui.panel.DockedPanel;

export class InstallAppDialog extends api.ui.dialog.ModalDialog {

    private dockedPanel: DockedPanel;

    private uploadAppPanel: UploadAppPanel;

    private marketAppPanel: MarketAppPanel;

    private onMarketLoaded;

    constructor() {
        super({
            title: new api.ui.dialog.ModalDialogHeader("Install Application")
        });

        this.addClass("install-application-dialog hidden");

        this.onMarketLoaded = this.centerMyself.bind(this);

        api.dom.Body.get().appendChild(this);
    }

    updateInstallApplications(installApplications: api.application.Application[]) {
        this.marketAppPanel.updateInstallApplications(installApplications);
    }

    doRender(): Q.Promise<boolean> {
        return super.doRender().then((rendered) => {

            if (!this.marketAppPanel) {
                this.marketAppPanel = new MarketAppPanel("market-app-panel");
            }

            if (!this.uploadAppPanel) {
                this.uploadAppPanel = new UploadAppPanel(this.getCancelAction(), "upload-app-panel");
                this.uploadAppPanel.onRendered((event) => {
                    this.uploadAppPanel.getApplicationInput().onKeyUp((event: KeyboardEvent) => {
                        if (event.keyCode === 27) {
                            this.getCancelAction().execute();
                        }
                    });

                    this.initUploaderListeners(this.uploadAppPanel);
                });
            }

            if (!this.dockedPanel) {
                this.dockedPanel = new DockedPanel();
                this.dockedPanel.addClass("install-app-docked-panel");
                this.dockedPanel.addItem("Enonic Market", true, this.marketAppPanel);
                this.dockedPanel.addItem("Upload", true, this.uploadAppPanel);

                this.appendChildToContentPanel(this.dockedPanel);
            }

            return rendered;
        });
    }

    private initUploaderListeners(uploadAppPanel: UploadAppPanel) {

        let uploadFailedHandler = (event: FileUploadFailedEvent<Application>, uploader: ApplicationUploaderEl) => {
            uploadAppPanel.getApplicationInput().showFailure(
                uploader.getFailure());
            this.resetFileInputWithUploader();
        };

        uploadAppPanel.getApplicationInput().onUploadFailed((event) => {
            uploadFailedHandler(event, uploadAppPanel.getApplicationInput().getUploader())
        });

        uploadAppPanel.getApplicationUploaderEl().onUploadFailed((event) => {
            uploadFailedHandler(event, uploadAppPanel.getApplicationUploaderEl())
        });

        let uploadCompletedHandler = (event: FileUploadCompleteEvent<Application>) => {
            if (event.getUploadItems()) {
                this.close();
            }
        };

        uploadAppPanel.getApplicationInput().onUploadCompleted(uploadCompletedHandler);
        uploadAppPanel.getApplicationUploaderEl().onUploadCompleted(uploadCompletedHandler);

        let uploadStartedHandler = (event: FileUploadStartedEvent<Application>) => {
            new api.application.ApplicationUploadStartedEvent(event.getUploadItems()).fire();
        };

        uploadAppPanel.getApplicationInput().onUploadStarted(uploadStartedHandler);
        uploadAppPanel.getApplicationUploaderEl().onUploadStarted(uploadStartedHandler);
    }

    show() {
        this.marketAppPanel.getMarketAppsTreeGrid().onLoaded(this.onMarketLoaded);
        this.resetFileInputWithUploader();
        super.show();
        this.marketAppPanel.loadGrid();
    }

    hide() {
        this.marketAppPanel.getMarketAppsTreeGrid().unLoaded(this.onMarketLoaded);
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
}
