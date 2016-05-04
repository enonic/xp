import "../../api.ts";

import Application = api.application.Application;
import {ApplicationTreeGrid} from "./ApplicationTreeGrid";
import {InstallAppPromptEvent} from "../installation/InstallAppPromptEvent";

export class InstallApplicationAction extends api.ui.Action {

    constructor(applicationTreeGrid: ApplicationTreeGrid) {
        super("Install");
        this.setEnabled(false);
        this.onExecuted(() => {
            const installedApplications: Application[] = applicationTreeGrid.getRoot().getCurrentRoot().treeToList().map(
                (node) => node.getData());
            new InstallAppPromptEvent(installedApplications).fire();
        });
    }
}
