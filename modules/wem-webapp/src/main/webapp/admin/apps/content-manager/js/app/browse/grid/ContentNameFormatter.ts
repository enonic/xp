module app_browse_grid {

    export class ContentNameFormatter extends api_app_browse_grid.NameFormatter<api_content.ContentSummary> {

        static format(row:number, cell:number, value:any, columnDef:any, item:api_content.ContentSummary):string {
            return api_app_browse_grid.NameFormatter.createHtml(item.getDisplayName(), item.getPath().toString(), item.getIconUrl());
        }
    }
}