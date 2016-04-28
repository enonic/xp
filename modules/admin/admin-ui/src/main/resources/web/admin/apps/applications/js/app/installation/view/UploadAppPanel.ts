import "../../../api.ts";

import ApplicationUploaderEl = api.application.ApplicationUploaderEl;
import InputEl = api.dom.InputEl;
import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
import Action = api.ui.Action;
import {ApplicationInput} from "./ApplicationInput";

export class UploadAppPanel extends api.ui.panel.Panel {

    private applicationInput: ApplicationInput;

    private applicationUploaderEl: api.application.ApplicationUploaderEl;

    constructor(cancelAction: Action, className?: string) {
        super(className);

        this.initApplicationInput(cancelAction);

        this.initApplicationUploader();

        this.onShown(() => {
            this.applicationInput.giveFocus();
        });
    }

    getApplicationInput(): ApplicationInput {
        return this.applicationInput;
    }

    getApplicationUploaderEl(): ApplicationUploaderEl {
        return this.applicationUploaderEl;
    }

    private initApplicationInput(cancelAction: Action) {
        this.applicationInput = new ApplicationInput(cancelAction, 'large').setPlaceholder("Paste link or drop files here");

        this.appendChild(this.applicationInput);
    }

    private initApplicationUploader() {

        var uploaderContainer = new api.dom.DivEl('uploader-container');
        this.appendChild(uploaderContainer);

        var uploaderMask = new api.dom.DivEl('uploader-mask');
        uploaderContainer.appendChild(uploaderMask);

        this.applicationUploaderEl = new api.application.ApplicationUploaderEl({
            params: {},
            name: 'application-uploader',
            showResult: false,
            allowMultiSelection: false,
            deferred: true  // wait till the window is shown
        });
        uploaderContainer.appendChild(this.applicationUploaderEl);

        var dragOverEl;
        // make use of the fact that when dragging
        // first drag enter occurs on the child element and after that
        // drag leave occurs on the parent element that we came from
        // meaning that to know when we left some element
        // we need to compare it to the one currently dragged over
        this.onDragEnter((event: DragEvent) => {
            var target = <HTMLElement> event.target;

            if (!!dragOverEl || dragOverEl == this.getHTMLElement()) {
                uploaderContainer.show();
            }
            dragOverEl = target;
        });

        this.onDragLeave((event: DragEvent) => {
            var targetEl = <HTMLElement> event.target;

            if (dragOverEl == targetEl) {
                uploaderContainer.hide();
            }
        });

        this.onDrop((event: DragEvent) => {
            uploaderContainer.hide();
        });
    }
}
