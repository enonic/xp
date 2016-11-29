import "../../api.ts";
import {MarketAppPanel} from "./view/MarketAppPanel";
import {UploadAppPanel} from "./view/UploadAppPanel";

import ApplicationKey = api.application.ApplicationKey;
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

    private dropzoneContainer: api.ui.uploader.DropzoneContainer;

    private onMarketLoaded;

    constructor() {
        super("Install Application");

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

                    this.dropzoneContainer = new api.ui.uploader.DropzoneContainer(true);
                    this.dropzoneContainer.hide();
                    this.appendChild(this.dropzoneContainer);

                    this.uploadAppPanel.getApplicationInput().getUploader().addDropzone(this.dropzoneContainer.getDropzone().getId());

                    this.initDragAndDropUploaderEvents();
                });
            }

            if (!this.dockedPanel) {
                this.dockedPanel = new DockedPanel();
                this.dockedPanel.addClass("install-app-docked-panel");
                this.dockedPanel.addItem("Enonic Market", true, this.marketAppPanel);
                this.dockedPanel.addItem("Upload", true, this.uploadAppPanel);

                this.dockedPanel.getNavigator().onNavigationItemSelected(() => this.centerMyself());

                this.appendChildToContentPanel(this.dockedPanel);
            }

            return rendered;
        });
    }

    // in order to toggle appropriate handlers during drag event
    // we catch drag enter on this element and trigger uploader to appear,
    // then catch drag leave on uploader's dropzone to get back to previous state
    private initDragAndDropUploaderEvents() {
        var dragOverEl;
        this.onDragEnter((event: DragEvent) => {
            var target = <HTMLElement> event.target;

            if (!!dragOverEl || dragOverEl == this.getHTMLElement()) {
                this.dropzoneContainer.show();
            }
            dragOverEl = target;
        });

        this.uploadAppPanel.getApplicationInput().getUploader().onDropzoneDragLeave(() => this.dropzoneContainer.hide());
        this.uploadAppPanel.getApplicationInput().getUploader().onDropzoneDrop(() => this.dropzoneContainer.hide());
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

        let uploadCompletedHandler = (event: FileUploadCompleteEvent<Application>) => {
            if (event.getUploadItems() && !this.hasClass("hidden")) {
            }
        };

        this.uploadAppPanel.getApplicationInput().onUploadCompleted(uploadCompletedHandler);

        let uploadStartedHandler = (event: FileUploadStartedEvent<Application>) => {
            new api.application.ApplicationUploadStartedEvent(event.getUploadItems()).fire();
            this.close();
        };

        this.uploadAppPanel.getApplicationInput().onUploadStarted(uploadStartedHandler);
    }

    show() {
        this.marketAppPanel.getMarketAppsTreeGrid().onLoaded(this.onMarketLoaded);
        this.resetFileInputWithUploader();
        super.show();
        this.removeClass("hidden");
        this.marketAppPanel.loadGrid();
    }

    hide() {
        this.marketAppPanel.getMarketAppsTreeGrid().unLoaded(this.onMarketLoaded);
        super.hide();
        this.addClass("hidden");
        this.removeClass("animated");
    }

    close() {
        this.uploadAppPanel.getApplicationInput().reset();
        super.close();
    }

    private resetFileInputWithUploader() {
        this.uploadAppPanel.getApplicationInput().reset();
    }
}
