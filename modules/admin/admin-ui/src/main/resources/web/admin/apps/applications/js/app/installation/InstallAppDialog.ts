import "../../api.ts";

import ApplicationKey = api.application.ApplicationKey;
import FileUploadCompleteEvent = api.ui.uploader.FileUploadCompleteEvent;
import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
import FileUploadFailedEvent = api.ui.uploader.FileUploadFailedEvent;
import ApplicationUploaderEl = api.application.ApplicationUploaderEl;
import Application = api.application.Application;

import DockedPanel = api.ui.panel.DockedPanel;

import {MarketAppPanel} from "./view/MarketAppPanel";
import {UploadAppPanel} from "./view/UploadAppPanel";

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

        this.initUploadAppPanel();

        this.initMarketAppPanel();

        api.dom.Body.get().appendChild(this);
    }

    updateInstallApplications(installApplications: api.application.Application[]) {
        this.marketAppPanel.updateInstallApplications(installApplications);
    }

    private initMarketAppPanel() {
        this.marketAppPanel = new MarketAppPanel("market-app-panel");
        this.marketAppPanel.onShown(() => {
            this.marketAppPanel.getMarketAppsTreeGrid().onLoaded(this.onMarketLoaded)
            this.centerMyself();
        });
    }

    private initUploadAppPanel() {

        this.uploadAppPanel = new UploadAppPanel(this.getCancelAction(), "upload-app-panel");
        this.uploadAppPanel.onShown(() => {
            this.centerMyself();
        });

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
        this.dockedPanel = installAppDockedPanel;
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

        let uploadCompletedHandler = (event: FileUploadCompleteEvent<Application>) => {
            if (event.getUploadItems()) {
                this.close();
            }
        };

        this.uploadAppPanel.getApplicationInput().onUploadCompleted(uploadCompletedHandler);

        let uploadStartedHandler = (event: FileUploadStartedEvent<Application>) => {
            new api.application.ApplicationUploadStartedEvent(event.getUploadItems()).fire();
        };

        this.uploadAppPanel.getApplicationInput().onUploadStarted(uploadStartedHandler);
    }

    open() {
        if (!this.dockedPanel) {
            this.initAndAppendInstallAppsTabsPanel();
        }
        super.open();
    }

    show() {
        this.resetFileInputWithUploader();
        super.show();
        this.marketAppPanel.loadGrid();
    }

    hide() {
        super.hide();
        this.uploadAppPanel.getApplicationInput().stop();
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
        this.uploadAppPanel.getApplicationInput().reset();
    }
}
