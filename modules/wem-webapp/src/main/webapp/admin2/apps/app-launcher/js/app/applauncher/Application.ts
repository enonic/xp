module app_launcher {

    export class Application {
        private name:string;
        private description:string;
        private iconUrl:string;
        private appUrl:string;
        private openTabs:number;

        constructor(name:string, appUrl:string, iconUrl:string, description?:string) {
            this.name = name;
            this.iconUrl = iconUrl;
            this.appUrl = appUrl;
            this.description = description;
            this.openTabs = 0;
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

        setOpenTabs(value:number):void {
            this.openTabs = value;
        }
    }

}
