import '../../../api.ts';
import {ContentTreeGrid} from '../ContentTreeGrid';

import Action = api.ui.Action;
import i18n = api.util.i18n;

export class DuplicateContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super(i18n('action.duplicate'));
        this.setEnabled(false);
        this.onExecuted(() => {
            grid.getSelectedDataList().forEach((elem) => {
                this.duplicate(elem.getContentSummary());
            });
        });
    }

    private duplicate(source: api.content.ContentSummary) {
        new api.content.resource.DuplicateContentRequest(source.getContentId()).sendAndParse().then((content: api.content.Content) => {
            // TODO: Replace the returning content with an id
            api.notify.showFeedback(`"${source.getDisplayName()}" duplicated`);
        });
    }
}
