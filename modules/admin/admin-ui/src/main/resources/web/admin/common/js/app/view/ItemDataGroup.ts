module api.app.view {

    export class ItemDataGroup extends api.dom.DivEl {

        private header: api.dom.H2El;

        private empty: boolean;

        constructor(title: string, className?: string) {
            super(!!className ? className + " item-data-group" : "item-data-group");
            this.header = new api.dom.H2El();
            this.header.getEl().setInnerHtml(title);
            this.appendChild(this.header);

            this.empty = true;
        }

        addDataList(header: string, ...datas: string[]) {
            this.addDataArray(header, datas);
        }

        addDataArray(header: string, datas: string[]) {
            var dataList = new api.dom.UlEl("data-list");

            if (header) {
                var headerElement = new api.dom.LiEl();
                headerElement.addClass("list-header");

                headerElement.getEl().setInnerHtml(header, false);
                dataList.appendChild(headerElement);
            }

            datas.forEach((data) => {
                var dataElement = new api.dom.LiEl();
                dataElement.getEl().setInnerHtml(data, false);
                dataList.appendChild(dataElement);
                this.empty = false;
            });

            this.appendChild(dataList);
        }

        isEmpty(): boolean {
            return this.empty;
        }
    }
}
