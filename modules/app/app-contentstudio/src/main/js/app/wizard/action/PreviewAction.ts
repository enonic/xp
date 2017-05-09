import '../../../api.ts';
import {BasePreviewAction} from '../../action/BasePreviewAction';
import {ContentWizardPanel} from '../ContentWizardPanel';

import RenderingMode = api.rendering.RenderingMode;

export class PreviewAction extends BasePreviewAction {

    constructor(wizard: ContentWizardPanel) {
        super('Preview');
        this.onExecuted(() => {
                if (wizard.hasUnsavedChanges()) {
                    wizard.setRequireValid(true);
                    wizard.saveChanges().then(content => this.openWindow(content)).catch(
                        (reason: any) => api.DefaultErrorHandler.handle(reason)).done();
                } else {
                    this.openWindow(wizard.getPersistedItem());
                }
            }
        );
    }
}
