import '../../api.ts';
import {ApplicationTreeGrid} from './ApplicationTreeGrid';
import {StopApplicationEvent} from './StopApplicationEvent';

import Application = api.application.Application;
import i18n = api.util.i18n;

export class StopApplicationAction extends api.ui.Action {

    constructor(applicationTreeGrid: ApplicationTreeGrid) {
        super(i18n('action.stop'));
        this.setEnabled(false);
        this.onExecuted(() => {
            let applications: Application[] = applicationTreeGrid.getSelectedDataList();
            new StopApplicationEvent(applications).fire();
        });
    }
}
