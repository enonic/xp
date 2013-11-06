module api_app{

    export class AppBarTabId {

        private mode:string;

        private id:string;

        static forNew(id?:string):AppBarTabId {
            return new AppBarTabId("new", id);
        }

        static forEdit(id:string):AppBarTabId {
            return new AppBarTabId("new", id);
        }

        static forView(id:string):AppBarTabId {
            return new AppBarTabId("view", id);
        }

        constructor(mode:string, id:string) {
            this.mode = mode;
            this.id = id;
        }

        changeToEditMode(id:string) {
            this.mode = "edit";
            this.id = id;
        }

        equals(other:AppBarTabId):boolean {
            return other.id == this.id && other.mode == this.mode;
        }
    }
}