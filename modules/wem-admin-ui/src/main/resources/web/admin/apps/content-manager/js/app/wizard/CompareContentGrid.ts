module app.wizard {

    import GridColumn = api.ui.grid.GridColumn;

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;


    export class CompareContentGrid extends api.app.browse.treegrid.TreeGrid<ContentSummary> {

        private content:api.content.Content;

        constructor(content:api.content.Content) {
            this.content = content;
            super({showToolbar: false}, "content-grid");

            var nameFormatter = (row:number, cell:number, value:any, columnDef:any, item:ContentSummary) => {
                var contentSummaryViewer = new ContentSummaryViewer();
                contentSummaryViewer.setObject(item);
                return contentSummaryViewer.toString();
            };

            var column1 = <GridColumn<ContentSummary>> {
                name: "Name",
                id: "displayName",
                field: "displayName",
                formatter: nameFormatter
            };

            this.setColumns([column1]);

            this.onLoaded(() => {
               this.selectAll();
            });
        }

        hasChildren(data: ContentSummary): boolean {
            return data.hasChildren();
        }

        fetchChildren(parent?: ContentSummary): Q.Promise<ContentSummary[]> {
            var deferred = Q.defer<ContentSummary[]>();

            deferred.resolve([this.content]);
            return deferred.promise;
        }
    }
}
