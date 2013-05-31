module API_ui {

    export class Component {

        private static counstructorCounter:number = 0;

        private id:string;

        constructor(parentName:string) {
            this.id = parentName + '-' + ++API_ui.Component.counstructorCounter;
        }

        getId():string {
            return this.id;
        }
    }
}
