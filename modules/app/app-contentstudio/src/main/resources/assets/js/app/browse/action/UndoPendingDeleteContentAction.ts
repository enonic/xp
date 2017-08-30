import '../../../api.ts';
import {ContentTreeGrid} from '../ContentTreeGrid';

import Action = api.ui.Action;
import UndoPendingDeleteContentRequest = api.content.resource.UndoPendingDeleteContentRequest;
import i18n = api.util.i18n;

export class UndoPendingDeleteContentAction extends Action {

    constructor(grid: ContentTreeGrid) {
        super(i18n('action.undoDelete'));

        this.setEnabled(true);
        this.setVisible(false);

        this.onExecuted(() => {
            let contents: api.content.ContentSummaryAndCompareStatus[]
                = grid.getSelectedDataList();
            new UndoPendingDeleteContentRequest(contents.map((content) => content.getContentId()))
                .sendAndParse().then((result: number) => UndoPendingDeleteContentRequest.showResponse(result));
        });
    }
}
