import '../../api.ts';
import {ApplicationTreeGrid} from './ApplicationTreeGrid';
import {StartApplicationEvent} from './StartApplicationEvent';

import Application = api.application.Application;
import i18n = api.util.i18n;

export class StartApplicationAction extends api.ui.Action {

    constructor(applicationTreeGrid: ApplicationTreeGrid) {
        super(i18n('action.start'));
        this.setEnabled(false);
        this.onExecuted(() => {
            let applications: Application[] = applicationTreeGrid.getSelectedDataList();
            new StartApplicationEvent(applications).fire();
        });
    }
}
