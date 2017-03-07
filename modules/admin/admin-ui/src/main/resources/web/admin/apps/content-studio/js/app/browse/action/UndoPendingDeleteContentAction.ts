import '../../../api.ts';
import {ContentTreeGrid} from '../ContentTreeGrid';

import Action = api.ui.Action;
import UndoPendingDeleteContentRequest = api.content.resource.UndoPendingDeleteContentRequest;

export class UndoPendingDeleteContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super('Undo delete');

        this.setEnabled(false);

        this.onExecuted(() => {
            let contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new UndoPendingDeleteContentRequest(contents.map((content) => content.getContentId()))
                .sendAndParse().then((result: number) => {
                if (result > 0) {
                    api.notify.showFeedback(`The item has been successfully undeleted`);
                } else {
                    api.notify.showWarning(`No item found to undelete`);
                }
            });
        });
    }
}
