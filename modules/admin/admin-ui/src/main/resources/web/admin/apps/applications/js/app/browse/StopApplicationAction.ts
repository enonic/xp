import "../../api.ts";
import {ApplicationTreeGrid} from "./ApplicationTreeGrid";
import {StopApplicationEvent} from "./StopApplicationEvent";

import Application = api.application.Application;

export class StopApplicationAction extends api.ui.Action {

    constructor(applicationTreeGrid: ApplicationTreeGrid) {
        super("Stop");
        this.setEnabled(false);
        this.onExecuted(() => {
            let applications: Application[] = applicationTreeGrid.getSelectedDataList();
            new StopApplicationEvent(applications).fire();
        });
    }
}
