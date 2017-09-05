import '../../api.ts';
import {MarketAppPanel} from './view/MarketAppPanel';
import {ApplicationInput} from './view/ApplicationInput';

import ApplicationKey = api.application.ApplicationKey;
import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
import FileUploadFailedEvent = api.ui.uploader.FileUploadFailedEvent;
import ApplicationUploaderEl = api.application.ApplicationUploaderEl;
import Application = api.application.Application;

import DockedPanel = api.ui.panel.DockedPanel;
import i18n = api.util.i18n;
import DivEl = api.dom.DivEl;

export class InstallAppDialog extends api.ui.dialog.ModalDialog {

    private marketAppPanel: MarketAppPanel;

    private dropzoneContainer: api.ui.uploader.DropzoneContainer;

    private onMarketLoaded: () => void;

    private applicationInput: ApplicationInput;

    private statusMessage: api.dom.DivEl;

    private clearButton: api.dom.ButtonEl;

    constructor() {
        super(i18n('dialog.install'));

        this.addClass('install-application-dialog hidden');

        this.statusMessage = new api.dom.DivEl('status-message');

        this.onMarketLoaded = () => {
            this.refreshStatusMessage();

            if (this.marketAppPanel.getMarketAppsTreeGrid().getGrid().getDataView().getLength() === 0) {
                this.statusMessage.addClass('empty');
                this.statusMessage.setHtml(i18n('market.noAppsFound'));
            } else {
                this.statusMessage.removeClass('empty');
            }
            this.statusMessage.addClass('loaded');

            this.centerMyself();
        };

        api.dom.Body.get().appendChild(this);
    }

    updateInstallApplications(installApplications: api.application.Application[]) {
        this.marketAppPanel.updateInstallApplications(installApplications);
    }

    doRender(): Q.Promise<boolean> {
        return super.doRender().then((rendered) => {

            this.applicationInput =
                new ApplicationInput(this.getCancelAction(), 'large').setPlaceholder(i18n('dialog.install.search'));
            this.onShown(() => {
                this.applicationInput.giveFocus();
                this.clearButton.addClass('hidden');
            });

            this.applicationInput.onTextValueChanged(() => {
                this.clearButton.toggleClass('hidden', api.util.StringHelper.isEmpty(this.applicationInput.getValue()));
            });

            this.applicationInput.onAppInstallFinished(() => {
                this.clearButton.toggleClass('hidden', api.util.StringHelper.isEmpty(this.applicationInput.getValue()));
            });

            this.applicationInput.onAppInstallFailed((message: string) => {
                this.clearButton.toggleClass('hidden', api.util.StringHelper.isEmpty(this.applicationInput.getValue()));

                this.statusMessage.addClass('empty failed');
                this.statusMessage.setHtml(message);

                setTimeout(this.centerMyself.bind(this), 100);
            });

            this.initUploaderListeners();

            this.dropzoneContainer = new api.ui.uploader.DropzoneContainer(true);
            this.dropzoneContainer.hide();
            this.appendChild(this.dropzoneContainer);

            this.applicationInput.getUploader().addDropzone(this.dropzoneContainer.getDropzone().getId());

            this.initDragAndDropUploaderEvents();

            if (!this.marketAppPanel) {
                this.marketAppPanel = new MarketAppPanel(this.applicationInput);
            }

            const marketAppPanelWrapper: DivEl = new DivEl('market-app-panel-wrapper');
            marketAppPanelWrapper.appendChild(this.marketAppPanel);

            this.appendChildToContentPanel(this.applicationInput);
            this.appendChildToContentPanel(this.statusMessage);
            this.appendChildToContentPanel(this.clearButton = this.createClearFilterButton());
            this.appendChildToContentPanel(marketAppPanelWrapper);

            return rendered;
        });
    }

    public createClearFilterButton(): api.dom.ButtonEl {
        let clearButton = new api.dom.ButtonEl();
        clearButton.addClass('clear-button hidden');
        clearButton.onClicked(() => {
            this.applicationInput.reset();
            this.marketAppPanel.getMarketAppsTreeGrid().refresh();
            clearButton.addClass('hidden');
            this.applicationInput.getTextInput().giveFocus();
        });
        return clearButton;
    }

    // in order to toggle appropriate handlers during drag event
    // we catch drag enter on this element and trigger uploader to appear,
    // then catch drag leave on uploader's dropzone to get back to previous state
    private initDragAndDropUploaderEvents() {
        let dragOverEl;
        this.onDragEnter((event: DragEvent) => {
            let target = <HTMLElement> event.target;

            if (!!dragOverEl || dragOverEl === this.getHTMLElement()) {
                this.dropzoneContainer.show();
            }
            dragOverEl = target;
        });

        this.applicationInput.getUploader().onDropzoneDragLeave(() => this.dropzoneContainer.hide());
        this.applicationInput.getUploader().onDropzoneDrop(() => this.dropzoneContainer.hide());
    }

    private initUploaderListeners() {

        let uploadFailedHandler = (event: FileUploadFailedEvent<Application>, uploader: ApplicationUploaderEl) => {
            api.notify.NotifyManager.get().showWarning(uploader.getFailure());

            this.resetFileInputWithUploader();
        };

        this.applicationInput.onUploadFailed((event) => {
            uploadFailedHandler(event, this.applicationInput.getUploader());
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
        this.removeClass('hidden');

        super.show();
        this.marketAppPanel.loadGrid();

        this.refreshStatusMessage();
    }

    hide() {
        super.hide();

        this.marketAppPanel.getMarketAppsTreeGrid().unLoaded(this.onMarketLoaded);
        this.statusMessage.removeClass('loaded');
        this.addClass('hidden');
        this.removeClass('animated');
        this.applicationInput.reset();
    }

    close() {
        this.applicationInput.reset();
        super.close();
    }

    private resetFileInputWithUploader() {
        this.applicationInput.reset();
    }

    private refreshStatusMessage() {
        this.statusMessage.removeClass('failed');
        this.statusMessage.setHtml(i18n('market.loadAppList'));
    }
}
