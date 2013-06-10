module api_ui_detailpanel {
    export class DetailPanel extends api_ui.DivEl {

        ext;

        constructor() {
            super("detailpanel");
            this.getEl().addClass("detailpanel");
            this.initExt();
        }

        private initExt() {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl,
                region: 'south'
            });
        }


    }

    export class DetailPanelBox extends api_ui.DivEl {
        private model;

        constructor(model:any, event?:api_event.Event) {
            super("detailpanel-box");
            this.model = model;
            this.getEl().addClass("detailpanel-box");
            this.setIcon(model.data.iconUrl, 32);
            this.setData(model.data.displayName, model.data.name);
            this.addRemoveButton(event);
        }

        private addRemoveButton(removeEvent?:api_event.Event) {
            var removeEl = document.createElement("div");
            removeEl.className = "remove";
            removeEl.innerHTML = "&times;";
            removeEl.addEventListener("click", (event) => {
                this.getEl().remove();
                if (removeEvent) {
                    removeEvent.fire();
                }
            });
            this.getEl().appendChild(removeEl);
        }

        private setIcon(iconUrl:string, size:number) {
            this.getEl().appendChild(api_util.ImageLoader.get(iconUrl + "?size=" + size, 32, 32));
        }

        private setData(title:string, subtitle:string) {
            var titleEl = document.createElement("h6");
            titleEl.innerHTML = title;

            var subtitleEl = document.createElement("small");
            subtitleEl.innerHTML = subtitle;
            titleEl.appendChild(subtitleEl);

            this.getEl().appendChild(titleEl);
            return titleEl;
        }
    }

}
