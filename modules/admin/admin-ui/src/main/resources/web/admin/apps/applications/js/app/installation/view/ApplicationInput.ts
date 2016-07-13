import "../../../api.ts";

import ApplicationUploaderEl = api.application.ApplicationUploaderEl;
import InputEl = api.dom.InputEl;
import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
import FileUploadCompleteEvent = api.ui.uploader.FileUploadCompleteEvent;
import FileUploadFailedEvent = api.ui.uploader.FileUploadFailedEvent;
import ApplicationInstallResult = api.application.ApplicationInstallResult;
import Action = api.ui.Action;
import Application = api.application.Application;
import {MarketAppsTreeGrid} from "./MarketAppsTreeGrid";

export class ApplicationInput extends api.dom.CompositeFormInputEl {

    private textInput: InputEl;
    private applicationUploaderEl: ApplicationUploaderEl;
    private lastTimeKeyPressedTimer;
    private LAST_KEY_PRESS_TIMEOUT: number;
    private mask: api.ui.mask.LoadMask;
    private cancelAction: Action;

    private errorPanel: api.form.ValidationRecordingViewer;

    private static APPLICATION_ADDRESS_MASK: string = "^(http|https)://\\S+";

    constructor(cancelAction: Action, className?: string, originalValue?: string) {

        this.textInput = new InputEl("text");

        this.applicationUploaderEl = new ApplicationUploaderEl({
            name: 'application-input-uploader',
            allowDrop: true,
            showResult: false,
            allowMultiSelection: true,
            deferred: true,  // wait till it's shown
            value: originalValue,
            showCancel: false,
            showReset: false
        });

        super(this.textInput, this.applicationUploaderEl);

        this.LAST_KEY_PRESS_TIMEOUT = 1500;
        this.cancelAction = cancelAction;

        this.applicationUploaderEl.onUploadStarted((event: api.ui.uploader.FileUploadStartedEvent<Application>) => {
            var names = event.getUploadItems().map((uploadItem: api.ui.uploader.UploadItem<Application>) => {
                return uploadItem.getName();
            });
            this.textInput.setValue(names.join(', '));
        });

        this.errorPanel = new api.form.ValidationRecordingViewer();
        this.appendChild(this.errorPanel);
        this.errorPanel.hide();

        this.onHidden(() => {
            this.errorPanel.hide();
        });

        this.addClass("file-input" + (className ? " " + className : ""));
        this.initUrlEnteredHandler();
    }

    private initUrlEnteredHandler() {
        this.onKeyDown((event) => {
            clearTimeout(this.lastTimeKeyPressedTimer);

            this.errorPanel.hide();
            switch (event.keyCode) {
            case 13: //enter
                this.startInstall();
                break;
            case 27: //esc
                break;
            default :
                this.lastTimeKeyPressedTimer = setTimeout(() => {
                    this.startInstall();
                }, this.LAST_KEY_PRESS_TIMEOUT);
                break;
            }
        });
    }

    private initMask() {
        if (!this.mask) {
            this.mask = new api.ui.mask.LoadMask(this);
            this.getParentElement().appendChild(this.mask);
        }
    }

    private startInstall() {
        if (!api.util.StringHelper.isEmpty(this.textInput.getValue())) {

            let url = this.textInput.getValue();
            if (api.util.StringHelper.testRegex(ApplicationInput.APPLICATION_ADDRESS_MASK, url)) {
                this.initMask();
                this.mask.show();

                this.installWithUrl(url);
                console.log("url: " + url);
            }
        }
    }

    private installWithUrl(url: string) {
        this.mask.show();
        new api.application.InstallUrlApplicationRequest(url).sendAndParse().then((result: api.application.ApplicationInstallResult)=> {

            let failure = result.getFailure();

            this.showFailure(failure);
            if (!failure) {
                this.cancelAction.execute();
            }

            this.mask.hide();

        }).catch((reason: any) => {
            this.mask.hide();
            api.DefaultErrorHandler.handle(reason);
        });
    }

    showFailure(failure) {
        if (failure) {
            this.errorPanel.setError(failure);
            this.errorPanel.show();
        } else {
            this.errorPanel.hide();
        }
    }

    setUploaderParams(params: {[key: string]: any}): ApplicationInput {
        this.applicationUploaderEl.setParams(params);
        return this;
    }

    getUploaderParams(): {[key: string]: string} {
        return this.applicationUploaderEl.getParams();
    }

    setPlaceholder(placeholder: string): ApplicationInput {
        this.textInput.setPlaceholder(placeholder);
        return this;
    }

    getPlaceholder(): string {
        return this.textInput.getPlaceholder();
    }

    reset(): ApplicationInput {
        this.textInput.reset();
        this.applicationUploaderEl.reset();
        return this;
    }

    stop(): ApplicationInput {
        this.applicationUploaderEl.stop();
        return this;
    }

    getUploader(): ApplicationUploaderEl {
        return this.applicationUploaderEl;
    }

    onUploadStarted(listener: (event: FileUploadStartedEvent<Application>) => void) {
        this.applicationUploaderEl.onUploadStarted(listener);
    }

    unUploadStarted(listener: (event: FileUploadStartedEvent<Application>) => void) {
        this.applicationUploaderEl.unUploadStarted(listener);
    }

    onUploadFailed(listener: (event: FileUploadFailedEvent<Application>) => void) {
        this.applicationUploaderEl.onUploadFailed(listener);
    }

    unUploadFailed(listener: (event: FileUploadFailedEvent<Application>) => void) {
        this.applicationUploaderEl.unUploadFailed(listener);
    }

    onUploadCompleted(listener: (event: FileUploadCompleteEvent<Application>) => void) {
        this.applicationUploaderEl.onUploadCompleted(listener);
    }

    unUploadCompleted(listener: (event: FileUploadCompleteEvent<Application>) => void) {
        this.applicationUploaderEl.unUploadCompleted(listener);
    }
}
