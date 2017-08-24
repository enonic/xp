module api.app.view {

    import SpanEl = api.dom.SpanEl;
    import StringHelper = api.util.StringHelper;

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
            const elements = datas.filter(text => !StringHelper.isBlank(text)).map(text => new SpanEl().setHtml(text, false));
            this.addDataElements(header, elements);
        }

        addDataElements(header:string, datas:api.dom.Element[]) {
            let dataList = new api.dom.UlEl('data-list');

            if (!datas || datas.length === 0) {
                return;
            }

            if (header) {
                this.addHeader(header, dataList);
            }

            datas.forEach((data) => {
                const dataElement = new api.dom.LiEl();
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
