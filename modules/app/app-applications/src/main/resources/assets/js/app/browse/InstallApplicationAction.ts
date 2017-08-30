import '../../api.ts';
import {ApplicationTreeGrid} from './ApplicationTreeGrid';
import {InstallAppPromptEvent} from '../installation/InstallAppPromptEvent';

import Application = api.application.Application;
import i18n = api.util.i18n;

export class InstallApplicationAction extends api.ui.Action {

    constructor(applicationTreeGrid: ApplicationTreeGrid) {
        super(i18n('action.install'));
        this.setEnabled(false);
        this.onExecuted(() => {
            const installedApplications: Application[] = applicationTreeGrid.getRoot().getCurrentRoot().treeToList().map(
                (node) => node.getData());
            new InstallAppPromptEvent(installedApplications).fire();
        });
    }
}
