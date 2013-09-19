module api_page{

    export class Part extends Component{

        private name:string;

        constructor(partJson:api_page_json.PartJson) {
            super();
            this.name = partJson.name;
        }
    }
}