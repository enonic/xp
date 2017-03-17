import '../../api.ts';
import {BaseContentModelEvent} from './BaseContentModelEvent';
import TreeNode = api.ui.treegrid.TreeNode;

export class MoveContentEvent extends BaseContentModelEvent {
    private rootNode: TreeNode<api.content.ContentSummaryAndCompareStatus>;

    constructor(model: api.content.ContentSummaryAndCompareStatus[], rootNode?: TreeNode<api.content.ContentSummaryAndCompareStatus>) {
        super(model);
        this.rootNode = rootNode;
    }

    getRootNode(): TreeNode<api.content.ContentSummaryAndCompareStatus> {
        return this.rootNode;
    }

    static on(handler: (event: MoveContentEvent) => void) {
        api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
    }

    static un(handler?: (event: MoveContentEvent) => void) {
        api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
    }
}
