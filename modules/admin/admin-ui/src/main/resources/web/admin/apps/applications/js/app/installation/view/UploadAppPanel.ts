import "../../../api.ts";

import ApplicationUploaderEl = api.application.ApplicationUploaderEl;
import InputEl = api.dom.InputEl;
import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
import Action = api.ui.Action;
import {ApplicationInput} from "./ApplicationInput";

export class UploadAppPanel extends api.ui.panel.Panel {

    private applicationInput: ApplicationInput;

    private dropzoneContainer: api.ui.uploader.DropzoneContainer;

    constructor(cancelAction: Action, className?: string) {
        super(className);

        this.initApplicationInput(cancelAction);

        this.initDragAndDropUploaderEvents();

        this.onShown(() => {
            this.applicationInput.giveFocus();
        });
    }

    getApplicationInput(): ApplicationInput {
        return this.applicationInput;
    }

    private initApplicationInput(cancelAction: Action) {
        this.dropzoneContainer = new api.ui.uploader.DropzoneContainer(true);
        this.dropzoneContainer.hide();
        this.appendChild(this.dropzoneContainer);

        this.applicationInput = new ApplicationInput(cancelAction, 'large').
            setPlaceholder("Paste link or drop files here");

        this.applicationInput.getUploader().addDropzone(this.dropzoneContainer.getDropzone().getId());

        this.appendChild(this.applicationInput);
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
}
