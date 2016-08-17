import "../../../api.ts";
import {ApplicationInput} from "./ApplicationInput";

import ApplicationUploaderEl = api.application.ApplicationUploaderEl;
import InputEl = api.dom.InputEl;
import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
import Action = api.ui.Action;

export class UploadAppPanel extends api.ui.panel.Panel {

    private cancelAction: Action;
    
    private applicationInput: ApplicationInput;

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

            this.applicationInput = new ApplicationInput(this.cancelAction, 'large').
                setPlaceholder("Paste link or drop files here");
    
            this.appendChild(this.applicationInput);
            
            return rendered;
        });
    }
}
