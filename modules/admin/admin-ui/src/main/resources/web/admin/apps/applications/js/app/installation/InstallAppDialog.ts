import "../../api.ts";
import {MarketAppPanel} from "./view/MarketAppPanel";
import {ApplicationInput} from "./view/ApplicationInput";

import ApplicationKey = api.application.ApplicationKey;
import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
import FileUploadFailedEvent = api.ui.uploader.FileUploadFailedEvent;
import ApplicationUploaderEl = api.application.ApplicationUploaderEl;
import Application = api.application.Application;

import DockedPanel = api.ui.panel.DockedPanel;

export class InstallAppDialog extends api.ui.dialog.ModalDialog {

    private marketAppPanel: MarketAppPanel;

    private dropzoneContainer: api.ui.uploader.DropzoneContainer;

    private onMarketLoaded;

    private applicationInput: ApplicationInput;

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

            this.applicationInput =
                new ApplicationInput(this.getCancelAction(), 'large').setPlaceholder("Search Enonic Market, paste url or upload directly");
            this.onShown(() => {
                this.applicationInput.giveFocus();
            });

            this.applicationInput.onKeyUp((event: KeyboardEvent) => {
                if (event.keyCode === 27) {
                    this.getCancelAction().execute();
                }
            });

            this.initUploaderListeners();

            this.dropzoneContainer = new api.ui.uploader.DropzoneContainer(true);
            this.dropzoneContainer.hide();
            this.appendChild(this.dropzoneContainer);

            this.applicationInput.getUploader().addDropzone(this.dropzoneContainer.getDropzone().getId());

            this.initDragAndDropUploaderEvents();

            if (!this.marketAppPanel) {
                this.marketAppPanel = new MarketAppPanel(this.applicationInput, "market-app-panel");
            }

            this.appendChildToContentPanel(this.applicationInput);
            this.appendChildToContentPanel(this.createClearFilterButton());
            this.appendChildToContentPanel(this.marketAppPanel);

            return rendered;
        });
    }

    public createClearFilterButton(): api.dom.ButtonEl {
        var clearButton = new api.dom.ButtonEl();
        clearButton.addClass("clear-button");
        clearButton.onClicked(() => {
            this.applicationInput.reset();
            this.marketAppPanel.getMarketAppsTreeGrid().refresh();
        });
        return clearButton;
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

        this.applicationInput.getUploader().onDropzoneDragLeave(() => this.dropzoneContainer.hide());
        this.applicationInput.getUploader().onDropzoneDrop(() => this.dropzoneContainer.hide());
    }

    private initUploaderListeners() {

        let uploadFailedHandler = (event: FileUploadFailedEvent<Application>, uploader: ApplicationUploaderEl) => {
            this.applicationInput.showFailure(
                uploader.getFailure());
            this.resetFileInputWithUploader();
        };

        this.applicationInput.onUploadFailed((event) => {
            uploadFailedHandler(event, this.applicationInput.getUploader())
        });

        let uploadStartedHandler = (event: FileUploadStartedEvent<Application>) => {
            new api.application.ApplicationUploadStartedEvent(event.getUploadItems()).fire();
            this.close();
        };

        this.applicationInput.onUploadStarted(uploadStartedHandler);
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
        this.applicationInput.reset();
    }

    close() {
        this.applicationInput.reset();
        super.close();
    }

    private resetFileInputWithUploader() {
        this.applicationInput.reset();
    }
}
