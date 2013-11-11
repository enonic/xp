module api_content_page{

    export class Part extends Component{

        private name:string;

        constructor(partJson:api_content_page_json.PartJson) {
            super();
            this.name = partJson.name;
        }
    }
}