module api.app.view {

    export class ItemDataGroup extends api.dom.DivEl {

        private header: api.dom.H2El;

        private empty: boolean;

        constructor(title: string, className?: string) {
            super(!!className ? className + ' item-data-group' : 'item-data-group');
            this.header = new api.dom.H2El();
            this.header.getEl().setInnerHtml(title);
            this.appendChild(this.header);

            this.empty = true;
        }

        addDataList(header: string, ...datas: string[]) {
            this.addDataArray(header, datas);
        }

        addDataArray(header: string, datas: string[]) {
            let dataList = new api.dom.UlEl('data-list');

            if (header) {
                this.addHeader(header, dataList);
            }

            datas.forEach((data) => {
                let dataElement = new api.dom.LiEl();
                dataElement.getEl().setInnerHtml(data, false);
                dataList.appendChild(dataElement);
                this.empty = false;
            });

            this.appendChild(dataList);
        }

        addDataElements(header:string, datas:api.dom.Element[]) {
            let dataList = new api.dom.UlEl('data-list');

            if (header) {
                this.addHeader(header, dataList);
            }

            datas.forEach((data) => {
                let dataElement = new api.dom.LiEl();
                dataElement.appendChild(data);
                dataList.appendChild(dataElement);
                this.empty = false;
            });

            this.appendChild(dataList);
        }

        private addHeader(header:string, dataList:api.dom.UlEl) {
            let headerElement = new api.dom.LiEl();
            headerElement.addClass('list-header');

            headerElement.getEl().setInnerHtml(header, false);
            dataList.appendChild(headerElement);
        }

        isEmpty(): boolean {
            return this.empty;
        }
    }
}
