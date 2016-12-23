module api.app {

    export enum ApplicationShowStatus {
        NOT_DISPLAYED,
        PREPARING,
        DISPLAYED
    }

    export class Application {
        private id: string;
        private name: string;
        private shortName: string;
        private iconUrl: string;
        private fullSizeIcon: boolean;
        private openTabs: number;
        private status: ApplicationShowStatus;
        private loaded: boolean;
        private path: api.rest.Path;
        private loadedListeners: {(): void}[] = [];
        private window: Window;

        constructor(id: string, name: string, shortName: string, icon: string, appFrame: api.dom.IFrameEl = null,
                    iconImage: boolean = false) {
            this.id = id;
            this.name = name;
            this.shortName = shortName;
            this.iconUrl = icon;
            this.fullSizeIcon = iconImage;
            this.openTabs = 0;
            this.status = ApplicationShowStatus.NOT_DISPLAYED;
        }

        static getApplication(): api.app.Application {
            return window.parent['getApplication'] ? window.parent['getApplication'](Application.getAppId()) : null;
        }

        static getAppId(): string {
            return window.frameElement ? new api.dom.ElementHelper(<HTMLElement>window.frameElement).getAttribute("data-wem-app-id") : null;
        }

        isLoaded(): boolean {
            return this.loaded;
        }

        getId(): string {
            return this.id;
        }

        getName(): string {
            return this.name;
        }

        getShortName(): string {
            return this.shortName;
        }

        getIconUrl(): string {
            return this.iconUrl;
        }

        getOpenTabs(): number {
            return this.openTabs;
        }

        getWindow(): Window {
            return this.window;
        }

        setWindow(window: Window) {
            this.window = window;
        }

        isFullSizeIcon(): boolean {
            return this.fullSizeIcon;
        }

        hide() {
            this.status = ApplicationShowStatus.NOT_DISPLAYED;
        }

        show() {
            this.status = ApplicationShowStatus.DISPLAYED;
        }

        setOpenTabs(value: number): Application {
            this.openTabs = value;
            return this;
        }

        setLoaded(value: boolean): Application {
            this.loaded = value;
            this.notifyLoaded();
            return this;
        }

        setDisplayingStatus(status: ApplicationShowStatus) {
            this.status = status;
        }

        setFullSizeIcon(value: boolean): Application {
            this.fullSizeIcon = value;
            return this;
        }

        setPath(path: api.rest.Path): Application {
            this.path = path;
            return this;
        }

        getPath(): api.rest.Path {
            return this.path;
        }

        isDisplayed(): boolean {
            return ApplicationShowStatus.DISPLAYED == this.status;
        }

        isPreparing(): boolean {
            return this.status == ApplicationShowStatus.PREPARING;
        }

        isNotDisplayed(): boolean {
            return this.status == ApplicationShowStatus.NOT_DISPLAYED;
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
