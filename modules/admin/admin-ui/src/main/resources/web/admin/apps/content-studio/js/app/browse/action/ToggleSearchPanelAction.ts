import "../../../api.ts";

import Action = api.ui.Action;
import {ToggleSearchPanelEvent} from "../ToggleSearchPanelEvent";

export class ToggleSearchPanelAction extends Action {

    constructor() {
        super("");
        this.onExecuted(() => {
            new ToggleSearchPanelEvent().fire();
        });
        this.setIconClass("icon-search3");
    }
}
