import '../../api.ts';
import {UserTreeGridItem} from './UserTreeGridItem';
import {UserTreeGridItemViewer} from './UserTreeGridItemViewer';
import TreeNode = api.ui.treegrid.TreeNode;

export class UserItemsRowFormatter {

    public static nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<UserTreeGridItem>) {
        let viewer = <UserTreeGridItemViewer>node.getViewer('displayName');
        if (!viewer) {
            viewer = new UserTreeGridItemViewer();
            viewer.setObject(node.getData(), node.calcLevel() > 1);
            node.setViewer('displayName', viewer);
        }
        return viewer.toString();
    }
}
