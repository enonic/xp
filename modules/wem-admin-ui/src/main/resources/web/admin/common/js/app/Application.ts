module api.app {

    export class Application {
        private id:string;
        private name:string;
        private shortName:string;
        private description:string;
        private iconUrl:string;
        private fullSizeIcon:boolean;
        private openTabs:number;
        private appFrame:api.dom.IFrameEl;
        private loaded:boolean;

        private loadedListeners: {(): void}[] = [];

        constructor(id: string, name: string, shortName: string, iconUrl: string, description?: string, appFrame: api.dom.IFrameEl = null,
                    fullSizeIcon: boolean = false) {
            this.id = id;
            this.name = name;
            this.shortName = shortName;
            this.iconUrl = iconUrl;
            this.fullSizeIcon = fullSizeIcon;
            this.description = description;
            this.openTabs = 0;
        }

        static getApplication(): api.app.Application {
            return window.parent['getApplication'] ? window.parent['getApplication'](Application.getAppId()) : null;
        }

        static getAppId(): string {
            return window.frameElement ? new api.dom.ElementHelper(<HTMLElement>window.frameElement).getAttribute("data-wem-app-id") : null;
        }

        isLoaded():boolean {
            return this.loaded && !!this.appFrame && !!this.appFrame.getParentElement();
        }

        getId():string {
            return this.id;
        }

        getName():string {
            return this.name;
        }

        getShortName(): string {
            return this.shortName;
        }

        getDescription():string {
            return this.description;
        }

        getIconUrl():string {
            return this.iconUrl;
        }

        getAppUrl():string {
            return api.util.getUri('admin?app=' + this.id);
        }

        getOpenTabs():number {
            return this.openTabs;
        }

        getAppFrame():api.dom.IFrameEl {
            if (!this.appFrame) {
                this.appFrame = new api.dom.IFrameEl();
                this.appFrame.getEl().setHeight('100%').setWidth('100%').getHTMLElement().style.border = '0';
                this.appFrame.setSrc(this.getAppUrl());
                this.appFrame.getEl().setAttribute('data-wem-app-id', this.id);
            }
            return this.appFrame;
        }

        getWindow() {
            return this.getAppFrame().getHTMLElement()["contentWindow"];
        }

        useFullSizeIcon():boolean {
            return this.fullSizeIcon;
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
            this.notifyLoaded();
        }

        setFullSizeIcon(value:boolean) {
            this.fullSizeIcon = value;
        }

        onLoaded(listener: () => void) {
            this.loadedListeners.push(listener);
        }

        unLoaded(listener: () => void) {
            this.loadedListeners = this.loadedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyLoaded() {
            this.loadedListeners.forEach((listener) => {
                listener();
            });
        }
    }

}
