module app_browse_grid {

    export class ContentDateTimeFormatter extends api_app_browse_grid.DateTimeFormatter<api_content.ContentSummary> {

        static format(row:number, cell:number, value:any, columnDef:any, item:api_content.ContentSummary):string {
            return api_app_browse_grid.DateTimeFormatter.createHtml(value);
        }
    }
}