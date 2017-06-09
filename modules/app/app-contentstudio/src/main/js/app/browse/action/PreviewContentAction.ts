import '../../../api.ts';
import {PreviewContentHandler} from './handler/PreviewContentHandler';
import {ContentTreeGrid} from '../ContentTreeGrid';
import {BasePreviewAction} from '../../action/BasePreviewAction';

import Action = api.ui.Action;
import RenderingMode = api.rendering.RenderingMode;
import ContentSummary = api.content.ContentSummary;
import ContentId = api.content.ContentId;
import i18n = api.util.i18n;

export class PreviewContentAction
    extends BasePreviewAction {

    private previewContentHandler: PreviewContentHandler;

    constructor(grid: ContentTreeGrid) {
        super(i18n('action.preview'), '');
        this.setEnabled(false);

        this.previewContentHandler = new PreviewContentHandler();

        this.onExecuted(() => {
            if (!this.previewContentHandler.isBlocked()) {
                let contentSummaries: ContentSummary[] = grid.getSelectedDataList().map(data => data.getContentSummary()).filter(
                    contentSummary => this.previewContentHandler.getRenderableIds().indexOf(contentSummary.getContentId().toString()) >= 0);

                this.openWindows(contentSummaries);
            } else {
                api.notify.showWarning(i18n('notify.preview', PreviewContentHandler.BLOCK_COUNT));
            }
        });
    }

    getPreviewHandler(): PreviewContentHandler {
        return this.previewContentHandler;
    }
}
