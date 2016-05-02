import "../../../api.ts";

import Action = api.ui.Action;
import {ContentTreeGrid} from "../ContentTreeGrid";

export class DuplicateContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super("Duplicate");
        this.setEnabled(false);
        this.onExecuted(() => {
            grid.getSelectedDataList().forEach((elem) => {
                this.duplicate(elem.getContentSummary());
            });
        });
    }

    private duplicate(source: api.content.ContentSummary) {
        new api.content.DuplicateContentRequest(source.getContentId()).sendAndParse().then((content: api.content.Content) => {
            // TODO: Replace the returning content with an id
            api.notify.showFeedback('\"' + source.getDisplayName() + '\" duplicated');
        })
    }
}
