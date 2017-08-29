import '../../api.ts';
import i18n = api.util.i18n;

export class CloseAction extends api.ui.Action {

    constructor(itemViewPanel: api.app.view.ItemViewPanel<api.content.ContentSummaryAndCompareStatus>,
                checkCanRemovePanel: boolean = true) {
        super(i18n('action.close'), 'mod+alt+f4');

        this.onExecuted(() => {
            itemViewPanel.close(checkCanRemovePanel);
        });
    }
}
