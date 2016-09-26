import "../../api.ts";
import {UserTreeGridItem} from "./UserTreeGridItem";
import {UserTreeGridItemViewer} from "./UserTreeGridItemViewer";
import TreeNode = api.ui.treegrid.TreeNode;

export class UserItemsRowFormatter {

    public static nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<UserTreeGridItem>) {
        var viewer = <UserTreeGridItemViewer>node.getViewer("displayName");
        if (!viewer) {
            var viewer = new UserTreeGridItemViewer();
            viewer.setObject(node.getData(), node.calcLevel() > 1);
            node.setViewer("displayName", viewer);
        }
        return viewer.toString();
    }
}
