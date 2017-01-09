import "../../../api.ts";
import {PreviewContentHandler} from "./handler/PreviewContentHandler";
import {ContentTreeGrid} from "../ContentTreeGrid";
import {BasePreviewAction} from "../../action/BasePreviewAction";

import Action = api.ui.Action;
import RenderingMode = api.rendering.RenderingMode;
import ContentSummary = api.content.ContentSummary;
import ContentId = api.content.ContentId;

export class PreviewContentAction extends BasePreviewAction {

    private previewContentHandler: PreviewContentHandler;

    constructor(grid: ContentTreeGrid) {
        super("Preview", "");
        this.setEnabled(false);

        this.previewContentHandler = new PreviewContentHandler();

        this.onExecuted(() => {
            if (!this.previewContentHandler.isBlocked()) {
                let contentSummaries: ContentSummary[] = grid.getSelectedDataList().map(data => data.getContentSummary()).filter(
                    contentSummary => this.previewContentHandler.getRenderableIds().indexOf(contentSummary.getContentId().toString()) >= 0);

                this.openWindows(contentSummaries);
            } else {
                api.notify.showWarning("Number of selected items exceeds maximum number allowed for preview ("
                                       + PreviewContentHandler.BLOCK_COUNT + "). Please deselect some of the items.");
            }
        });
    }

    getPreviewHandler(): PreviewContentHandler {
        return this.previewContentHandler;
    }
}
