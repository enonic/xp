import '../../../api.ts';
import {SortContentDialog} from '../SortContentDialog';
import {SaveSortedContentEvent} from '../SaveSortedContentEvent';
import i18n = api.util.i18n;

export class SaveSortedContentAction extends api.ui.Action {

    constructor(dialog: SortContentDialog) {
        super(i18n('action.save'));
        this.setEnabled(true);

        this.onExecuted(() => {
            new SaveSortedContentEvent(dialog.getContent().getContentSummary()).fire();
        });
    }
}
