import '../../../api.ts';
import {ShowIssuesDialogEvent} from '../ShowIssuesDialogEvent';

export class ShowIssuesDialogAction extends api.ui.Action {

    constructor() {
        super();
        this.setEnabled(true);
        this.onExecuted(() => {
            new ShowIssuesDialogEvent().fire();
        });
    }
}
