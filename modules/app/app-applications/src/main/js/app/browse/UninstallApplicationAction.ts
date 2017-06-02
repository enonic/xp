import '../../api.ts';
import {ApplicationTreeGrid} from './ApplicationTreeGrid';
import {UninstallApplicationDialog} from './UninstallApplicationDialog';

import Application = api.application.Application;
import i18n = api.util.i18n;

export class UninstallApplicationAction extends api.ui.Action {

    constructor(applicationTreeGrid: ApplicationTreeGrid) {
        super(i18n('action.uninstall'));
        this.setEnabled(false);

        this.onExecuted(() => {
            let applications: Application[] = applicationTreeGrid.getSelectedDataList();
            new UninstallApplicationDialog(applications).open();
        });
    }
}
