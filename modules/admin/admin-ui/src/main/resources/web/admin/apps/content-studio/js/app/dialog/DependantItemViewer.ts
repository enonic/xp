import "../../api.ts";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class DependantItemViewer extends api.ui.NamesAndIconViewer<ContentSummaryAndCompareStatus> {

    constructor() {
        super("dependant-item-viewer");
    }

    resolveDisplayName(object: ContentSummaryAndCompareStatus): string {
        let pendingDelete = (api.content.CompareStatus.PENDING_DELETE == object.getCompareStatus());

        this.toggleClass("pending-delete", pendingDelete);
        return object.getPath().toString();
    }

    resolveSubName(object: ContentSummaryAndCompareStatus, relativePath: boolean = false): string {
        return super.resolveSubName(object, relativePath);
    }

    resolveIconUrl(object: ContentSummaryAndCompareStatus): string {
        if(! object.getType().isImage()) {
            return new api.content.util.ContentIconUrlResolver().setContent(object.getContentSummary()).resolve();
        }
    }
    resolveIconClass (object: ContentSummaryAndCompareStatus): string {
        if(object.getType().isImage()) {
            return "image";
        }
    }
}
