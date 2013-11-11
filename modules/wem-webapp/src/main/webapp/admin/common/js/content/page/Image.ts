module api_content_page{

    export class Image extends Component {

        private url:string;

        constructor(imageJson:api_content_page_json.ImageJson) {
            super();
            this.url = imageJson.url;
        }
    }
}