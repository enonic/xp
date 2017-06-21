import '../../api.ts';
import i18n = api.util.i18n;

export class EditAction extends api.ui.Action {

    constructor(panel: api.app.view.ItemViewPanel<api.content.ContentSummaryAndCompareStatus>) {
        super(i18n('action.edit'));
        this.onExecuted(() => {
            new api.content.event.EditContentEvent([panel.getItem().getModel()]).fire();
        });
    }
}
