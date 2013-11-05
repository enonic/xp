module app_launcher {

    export class Application {
        private id:string;
        private name:string;
        private description:string;
        private iconUrl:string;
        private openTabs:number;
        private appFrame:api_dom.IFrameEl;
        private loaded:boolean;

        constructor(id: string, name:string, iconUrl:string, description?:string, appFrame:api_dom.IFrameEl = null) {
            this.id = id;
            this.name = name;
            this.iconUrl = iconUrl;
            this.description = description;
            this.openTabs = 0;
        }

        isLoaded():boolean {
            return this.loaded;
        }

        getId():string {
            return this.id;
        }

        getName():string {
            return this.name;
        }

        getDescription():string {
            return this.description;
        }

        getIconUrl():string {
            return this.iconUrl;
        }

        getAppUrl():string {
            return api_util.getUri('admin?app=' + this.id);
        }

        getOpenTabs():number {
            return this.openTabs;
        }

        getAppFrame():api_dom.IFrameEl {
            if (!this.appFrame) {
                this.appFrame = new api_dom.IFrameEl();
                this.appFrame.getEl().setHeight('100%').setWidth('100%').getHTMLElement().style.border = '0';
                this.appFrame.setSrc(this.getAppUrl());
                this.appFrame.getEl().setAttribute('data-wem-app', this.id);
            }
            return this.appFrame;
        }

        getWindow() {
            return this.getAppFrame().getHTMLElement()["contentWindow"];
        }

        hasAppFrame():boolean {
            return this.appFrame != null;
        }

        hide() {
            if (this.appFrame) {
                this.appFrame.hide();
            }
        }

        setOpenTabs(value:number):void {
            this.openTabs = value;
        }

        setLoaded(value:boolean) {
            this.loaded = value;
        }
    }

}
