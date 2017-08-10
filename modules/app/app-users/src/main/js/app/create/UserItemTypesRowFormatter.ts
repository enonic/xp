import '../../api.ts';
import {UserTypeTreeGridItem} from './UserTypeTreeGridItem';
import {UserTypesTreeGridItemViewer} from './UserTypesTreeGridItemViewer';
import TreeNode = api.ui.treegrid.TreeNode;

export class UserItemTypesRowFormatter {

    public static nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<UserTypeTreeGridItem>) {
        let viewer = <UserTypesTreeGridItemViewer>node.getViewer('displayName');
        if (!viewer) {
            const isRootNode = node.calcLevel() === 1;
            viewer = new UserTypesTreeGridItemViewer(isRootNode);
            viewer.setObject(node.getData());
            node.setViewer('displayName', viewer);
        }
        return viewer.toString();
    }
}
