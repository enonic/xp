import "../../api.ts";
import {ApplicationTreeGrid} from "./ApplicationTreeGrid";
import {StartApplicationEvent} from "./StartApplicationEvent";

import Application = api.application.Application;

export class StartApplicationAction extends api.ui.Action {

    constructor(applicationTreeGrid: ApplicationTreeGrid) {
        super("Start");
        this.setEnabled(false);
        this.onExecuted(() => {
            let applications: Application[] = applicationTreeGrid.getSelectedDataList();
            new StartApplicationEvent(applications).fire();
        });
    }
}
