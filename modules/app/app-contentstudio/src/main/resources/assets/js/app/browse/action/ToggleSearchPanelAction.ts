import '../../../api.ts';
import {ToggleSearchPanelEvent} from '../ToggleSearchPanelEvent';

import Action = api.ui.Action;

export class ToggleSearchPanelAction extends Action {

    constructor() {
        super('');
        this.onExecuted(() => {
            new ToggleSearchPanelEvent().fire();
        });
        this.setIconClass('icon-search3');
    }
}
