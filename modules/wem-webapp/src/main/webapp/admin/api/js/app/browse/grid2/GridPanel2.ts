module api_app_browse_grid2 {

    export class GridPanel2 extends api_ui.Panel {

        private nameFormatter:(row:number, cell:number, value:any, columnDef:any, dataContext:Slick.SlickData) => string;

        constructor() {
            super("GridPanel2");
            this.addClass("grid-panel2");

            this.nameFormatter = (row:number, cell:number, value:any, columnDef:any, item:api_content.ContentSummary) => {
                var rowEl = new api_dom.DivEl();

                var icon = new api_dom.ImgEl();
                icon.getEl().setSrc(item.getIconUrl());
                icon.getEl().setWidth("32px");
                icon.getEl().setHeight("32px");

                var displayName = new api_dom.H6El();
                displayName.getEl().setInnerHtml(item.getDisplayName());

                var path = new api_dom.PEl();
                path.getEl().setInnerHtml(item.getPath().toString());

                var textContainer = new api_dom.DivEl();
                textContainer.addClass("text");
                textContainer.appendChild(displayName);
                textContainer.appendChild(path);

                rowEl.appendChild(icon);
                rowEl.appendChild(textContainer);

                return rowEl.toString();
            };
        }

        getNameFormatter() {
            return this.nameFormatter;
        }
    }
}
