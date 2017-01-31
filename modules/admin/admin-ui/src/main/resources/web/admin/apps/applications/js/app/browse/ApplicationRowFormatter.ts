import '../../api.ts';
import TreeNode = api.ui.treegrid.TreeNode;
import Application = api.application.Application;
import ApplicationViewer = api.application.ApplicationViewer;
import ApplicationUploadMock = api.application.ApplicationUploadMock;

export class ApplicationRowFormatter {

    public static nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<Application>) {
        let viewer = <ApplicationViewer>node.getViewer('name');
        if (!viewer) {
            viewer = new ApplicationViewer();
            viewer.setObject(node.getData());
            node.setViewer('name', viewer);
        }
        return viewer.toString();
    }

    public static stateFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<Application>) {
        const data = node.getData();
        const statusEl = new api.dom.DivEl();

        if (data instanceof Application) {   // default node
            statusEl.getEl().setText(value);
        } else if (api.ObjectHelper.iFrameSafeInstanceOf(data, ApplicationUploadMock)) {   // uploading node
            const status = new api.ui.ProgressBar((<any>data).getUploadItem().getProgress());
            statusEl.appendChild(status);
        }

        return statusEl.toString();
    }
}
