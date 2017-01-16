import '../../../api.ts';
import {SortContentDialog} from '../SortContentDialog';
import {SaveSortedContentEvent} from '../SaveSortedContentEvent';

export class SaveSortedContentAction extends api.ui.Action {

    constructor(dialog: SortContentDialog) {
        super('Save');
        this.setEnabled(true);

        this.onExecuted(() => {
            new SaveSortedContentEvent(dialog.getContent().getContentSummary()).fire();
        });
    }
}
