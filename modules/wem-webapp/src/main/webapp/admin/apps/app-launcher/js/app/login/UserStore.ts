module app_login {

    export class UserStore {
        private name:string;
        private id:string;

        constructor(name:string, id:string) {
            this.name = name;
            this.id = id;
        }

        getName():string {
            return this.name;
        }

        getId():string {
            return this.id;
        }
    }

}
