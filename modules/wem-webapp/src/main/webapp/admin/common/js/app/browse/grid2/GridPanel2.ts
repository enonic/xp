module api.app.browse.grid2 {

    export class GridPanel2 extends api.ui.Panel {

        private nameFormatter: (row: number, cell: number, value: any, columnDef: any, dataContext: Slick.SlickData) => string;

        constructor() {
            super("GridPanel2");
            this.addClass("grid-panel2");

            this.nameFormatter = (row: number, cell: number, value: any, columnDef: any, item: api.content.ContentSummary) => {
                var rowEl = new api.dom.DivEl();

                var icon = new api.dom.ImgEl();
                icon.getEl().setSrc(item.getIconUrl());
                icon.getEl().setWidth("32px");
                icon.getEl().setHeight("32px");

                var displayName = new api.dom.H6El();
                displayName.getEl().setInnerHtml(item.getDisplayName());

                var path = new api.dom.PEl();
                path.getEl().setInnerHtml(item.getPath().toString());

                var textContainer = new api.dom.DivEl();
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
