module api.content.util {

    import TreeNode = api.ui.treegrid.TreeNode;
    import ContentAndStatusTreeSelectorItem = api.content.resource.ContentAndStatusTreeSelectorItem;
    import Option = api.ui.selector.Option;

    export class ContentRowFormatter {

        public static nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {
            const data = node.getData();
            if (data.getContentSummary() || data.getUploadItem()) {
                let viewer = <ContentSummaryAndCompareStatusViewer> node.getViewer('name');
                if (!viewer) {
                    viewer = new ContentSummaryAndCompareStatusViewer();
                    node.setViewer('name', viewer);
                }
                viewer.setObject(node.getData(), node.calcLevel() > 1);
                return viewer ? viewer.toString() : '';
            }

            return '';
        }

        public static orderFormatter(row: number, cell: number, value: any, columnDef: any,
                                     node: TreeNode<ContentSummaryAndCompareStatus>) {
            let wrapper = new api.dom.SpanEl();

            if (!api.util.StringHelper.isBlank(value)) {
                wrapper.getEl().setTitle(value);
            }

            if (node.getData().getContentSummary()) {
                let childOrder = node.getData().getContentSummary().getChildOrder();
                let icon;
                if (!childOrder.isDefault()) {
                    if (!childOrder.isManual()) {
                        if (childOrder.isDesc()) {
                            icon = new api.dom.DivEl('icon-arrow-up2 sort-dialog-trigger');
                        } else {
                            icon = new api.dom.DivEl('icon-arrow-down4 sort-dialog-trigger');
                        }
                    } else {
                        icon = new api.dom.DivEl(api.StyleHelper.getCommonIconCls('menu') + ' sort-dialog-trigger');
                    }
                    wrapper.getEl().setInnerHtml(icon.toString(), false);
                }
            }
            return wrapper.toString();
        }

        public static statusFormatter(row: number, cell: number, value: any, columnDef: any,
                                      node: TreeNode<ContentSummaryAndCompareStatus>) {

            const data = node.getData();

            return ContentRowFormatter.doStatusFormat(data, value);
        }

        public static statusSelectorFormatter(row: number, cell: number, value: ContentAndStatusTreeSelectorItem, columnDef: any,
                                              node: TreeNode<Option<ContentAndStatusTreeSelectorItem>>) {

            if (value.getCompareStatus() || value.getPublishStatus()) {
                return ContentRowFormatter.doStatusFormat(
                    ContentSummaryAndCompareStatus.fromContentAndCompareAndPublishStatus(value.getContent(),
                        value.getCompareStatus(),
                        value.getPublishStatus()), value.getCompareStatus());
            }

            return '';
        }

        private static doStatusFormat(data: ContentSummaryAndCompareStatus, value: any): string {

            if (data && data.getContentSummary()) {
                const publishStatus: PublishStatus = data.getPublishStatus();

                let compareStatusText = api.content.CompareStatusFormatter.formatStatusFromContent(data);

                if (PublishStatus[publishStatus] && (publishStatus === PublishStatus.PENDING || publishStatus === PublishStatus.EXPIRED)) {
                    const compareStatusCls = ContentRowFormatter.makeClassName(CompareStatus[value]);
                    const publishStatusCls = ContentRowFormatter.makeClassName(PublishStatus[publishStatus]);

                    const statusEl = new api.dom.DivEl(compareStatusCls + ' ' + publishStatusCls);
                    statusEl.getEl().setText(compareStatusText);

                    const publishStatusEl = new api.dom.DivEl(compareStatusCls + ' ' + publishStatusCls);
                    const publishStatusText = api.content.PublishStatusFormatter.formatStatus(publishStatus);

                    publishStatusEl.getEl().setText('(' + publishStatusText + ')');

                    return statusEl.toString() + publishStatusEl.toString();
                } else {
                    const statusEl = new api.dom.SpanEl();
                    if (CompareStatus[value]) {
                        statusEl.addClass(ContentRowFormatter.makeClassName(compareStatusText));
                    }
                    statusEl.getEl().setText(compareStatusText);
                    return statusEl.toString();
                }
            } else if (data.getUploadItem()) { // uploading node
                const compareStatusText = new api.ui.ProgressBar(data.getUploadItem().getProgress());
                return new api.dom.SpanEl().appendChild(compareStatusText).toString();
            }
        }

        public static makeClassName(entry: string): string {
            return entry.toLowerCase().replace('_', '-').replace(' ', '_') || 'unknown';
        }
    }
}
