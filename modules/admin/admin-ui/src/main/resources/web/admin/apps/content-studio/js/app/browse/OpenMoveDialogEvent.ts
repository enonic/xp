import '../../api.ts';
import TreeNode = api.ui.treegrid.TreeNode;

export class OpenMoveDialogEvent extends api.event.Event {
    private content: api.content.ContentSummary[];
    private rootNode: TreeNode<api.content.ContentSummaryAndCompareStatus>;

    constructor(content: api.content.ContentSummary[], rootNode?: TreeNode<api.content.ContentSummaryAndCompareStatus>) {
        super();
        this.content = content;
        this.rootNode = rootNode;
    }

    getContentSummaries(): api.content.ContentSummary[] {
        return this.content;
    }

    getRootNode(): TreeNode<api.content.ContentSummaryAndCompareStatus> {
        return this.rootNode;
    }

    static on(handler: (event: OpenMoveDialogEvent) => void, contextWindow: Window = window) {
        api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
    }

    static un(handler?: (event: OpenMoveDialogEvent) => void, contextWindow: Window = window) {
        api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
    }
}
