module app_new {

    export class ContentTypeListItem {

        private name:string;

        private displayName:string;

        private iconUrl:string;

        constructor(name:string, displayName:string, iconUrl:string) {
            this.name = name;
            this.displayName = displayName;
            this.iconUrl = iconUrl;
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
    }
}