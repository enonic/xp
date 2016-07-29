import "../../../api.ts";
import {ApplicationInput} from "./ApplicationInput";

import ApplicationUploaderEl = api.application.ApplicationUploaderEl;
import InputEl = api.dom.InputEl;
import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
import Action = api.ui.Action;

export class UploadAppPanel extends api.ui.panel.Panel {

    private cancelAction: Action;
    
    private applicationInput: ApplicationInput;

    private dropzoneContainer: api.ui.uploader.DropzoneContainer;

    constructor(cancelAction: Action, className?: string) {
        super(className);

        this.cancelAction = cancelAction;

        this.onShown(() => {
            this.applicationInput.giveFocus();
        });
    }

    getApplicationInput(): ApplicationInput {
        return this.applicationInput;
    }

    doRender(): Q.Promise<boolean> {
        return super.doRender().then((rendered) => {

            this.dropzoneContainer = new api.ui.uploader.DropzoneContainer(true);
            this.dropzoneContainer.hide();
            this.appendChild(this.dropzoneContainer);
    
            this.applicationInput = new ApplicationInput(cancelAction, 'large').
                setPlaceholder("Paste link or drop files here");
    
            this.applicationInput.getUploader().addDropzone(this.dropzoneContainer.getDropzone().getId());
    
            this.appendChild(this.applicationInput);

            this.initDragAndDropUploaderEvents();
            
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

        this.applicationInput.getUploader().onDropzoneDragLeave(() => this.dropzoneContainer.hide());
        this.applicationInput.getUploader().onDropzoneDrop(() => this.dropzoneContainer.hide());
    }
}
