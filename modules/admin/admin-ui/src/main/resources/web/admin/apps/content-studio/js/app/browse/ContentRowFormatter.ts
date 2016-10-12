import "../../api.ts";
import TreeNode = api.ui.treegrid.TreeNode;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;
import ContentSummaryAndCompareStatusViewer = api.content.ContentSummaryAndCompareStatusViewer;

export class ContentRowFormatter {

    public static nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {
        const data = node.getData();
        if (data.getContentSummary() || data.getUploadItem()) {
            let viewer = <ContentSummaryAndCompareStatusViewer> node.getViewer("name");
            if (!viewer) {
                viewer = new ContentSummaryAndCompareStatusViewer();
                node.setViewer("name", viewer);
            }
            viewer.setObject(node.getData(), node.calcLevel() > 1);
            return viewer ? viewer.toString() : "";
        }

        return "";
    }

    public static orderFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {
        var wrapper = new api.dom.SpanEl();

        if (!api.util.StringHelper.isBlank(value)) {
            wrapper.getEl().setTitle(value);
        }

        if (node.getData().getContentSummary()) {
            var childOrder = node.getData().getContentSummary().getChildOrder();
            var icon;
            if (!childOrder.isDefault()) {
                if (!childOrder.isManual()) {
                    if (childOrder.isDesc()) {
                        icon = new api.dom.DivEl("icon-arrow-up2 sort-dialog-trigger");
                    } else {
                        icon = new api.dom.DivEl("icon-arrow-down4 sort-dialog-trigger");
                    }
                } else {
                    icon = new api.dom.DivEl(api.StyleHelper.getCommonIconCls("menu") + " sort-dialog-trigger");
                }
                wrapper.getEl().setInnerHtml(icon.toString(), false);
            }
        }
        return wrapper.toString();
    }

    public static statusFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {

        var data = node.getData(),
            status,
            statusEl = new api.dom.SpanEl();

        if (!!data.getContentSummary()) {   // default node
            var compareStatus: CompareStatus = CompareStatus[CompareStatus[value]];

            status = api.content.CompareStatusFormatter.formatStatus(compareStatus);

            if (!!CompareStatus[value]) {
                statusEl.addClass(CompareStatus[value].toLowerCase().replace("_", "-") || "unknown");
            }

            statusEl.getEl().setText(status);
        } else if (!!data.getUploadItem()) {   // uploading node
            status = new api.ui.ProgressBar(data.getUploadItem().getProgress());
            statusEl.appendChild(status);
        }

        return statusEl.toString();
    }
}
