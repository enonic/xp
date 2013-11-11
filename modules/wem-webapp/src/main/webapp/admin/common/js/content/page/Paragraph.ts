module api_content_page{

    export class Paragraph extends Component {

        private text:string;

        constructor(paragraphJson:api_content_page_json.ParagraphJson) {
            super();
            this.text = paragraphJson.text;
        }

        getText():string {
            return this.text;
        }

    }
}