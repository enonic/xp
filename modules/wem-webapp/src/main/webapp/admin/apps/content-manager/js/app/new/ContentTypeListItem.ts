module app_new {

    export class ContentTypeListItem {

        private name:string;

        private displayName:string;

        private iconUrl:string;

        private siteRoot:boolean;

        constructor(name:string, displayName:string, iconUrl:string, root?:boolean) {
            this.name = name;
            this.displayName = displayName;
            this.iconUrl = iconUrl;
            this.siteRoot = root || false;
        }

        getName() {
            return this.name;
        }

        getDisplayName() {
            return this.displayName;
        }

        getIconUrl() {
            return this.iconUrl;
        }

        isSiteRoot() {
            return this.siteRoot;
        }
    }
}