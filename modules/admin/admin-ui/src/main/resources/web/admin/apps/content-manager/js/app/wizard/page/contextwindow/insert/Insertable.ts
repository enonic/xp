module app.wizard.page.contextwindow.insert {

    export class Insertable {

        private name: string;

        private description: string;

        private displayName: string;

        private iconCls: string;

        public setName(value: string): Insertable {
            this.name = value;
            return this;
        }

        public getName(): string {
            return this.name;
        }

        public setDescription(value: string): Insertable {
            this.description = value;
            return this;
        }

        public getDescription(): string {
            return this.description;
        }

        public setDisplayName(value: string): Insertable {
            this.displayName = value;
            return this;
        }

        public getDisplayName(): string {
            return this.displayName;
        }

        public setIconCls(value: string): Insertable {
            this.iconCls = api.StyleHelper.getIconCls(value);
            return this;
        }

        public getIconCls(): string {
            return this.iconCls;
        }

    }
}
