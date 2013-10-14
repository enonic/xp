module app_launcher {

    export class Application {
        private name:string;
        private description:string;
        private iconUrl:string;
        private appUrl:string;
        private openTabs:number;
        private appFrame:api_dom.IFrameEl;
        private loaded:boolean;

        constructor(name:string, appUrl:string, iconUrl:string, description?:string, appFrame:api_dom.IFrameEl = null) {
            this.name = name;
            this.iconUrl = iconUrl;
            this.appUrl = appUrl;
            this.description = description;
            this.openTabs = 0;
        }

        isLoaded():boolean {
            return this.loaded;
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
            return this.appUrl;
        }

        getOpenTabs():number {
            return this.openTabs;
        }

        getAppFrame():api_dom.IFrameEl {
            if (!this.appFrame) {
                this.appFrame = new api_dom.IFrameEl();
                this.appFrame.getEl().setHeight('100%').setWidth('100%').getHTMLElement().style.border = '0';
                this.appFrame.setSrc(this.appUrl);
                this.appFrame.getEl().setAttribute('data-wem-app', this.name);
            }
            return this.appFrame;
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
