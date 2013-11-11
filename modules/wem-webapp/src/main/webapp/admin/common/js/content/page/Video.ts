module api_content_page{

    export class Video extends Component {

        private url:string;

        constructor(videoJson:api_content_page_json.VideoJson) {
            super();
            this.url = videoJson.url;
        }
    }
}