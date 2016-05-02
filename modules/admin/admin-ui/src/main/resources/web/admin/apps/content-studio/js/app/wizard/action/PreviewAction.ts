import "../../../api.ts";

import RenderingMode = api.rendering.RenderingMode;
import {BasePreviewAction} from "../../action/BasePreviewAction";
import {ContentWizardPanel} from "../ContentWizardPanel";

export class PreviewAction extends BasePreviewAction {

    constructor(wizard: ContentWizardPanel) {
        super("Preview");
        this.onExecuted(() => {
                var previewWindow = this.openBlankWindow(wizard.getPersistedItem());
                if (wizard.hasUnsavedChanges()) {
                    wizard.setRequireValid(true);
                    wizard.saveChanges().then(content => this.updateLocation(previewWindow, content)).catch(
                        (reason: any) => api.DefaultErrorHandler.handle(reason)).done();
                } else {
                    this.updateLocation(previewWindow, wizard.getPersistedItem());
                }
            }
        );
    }
}
