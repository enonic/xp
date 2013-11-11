module api_page{

    export class Paragraph extends Component {

        private text:string;

        constructor(paragraphJson:api_page_json.ParagraphJson) {
            super();
            this.text = paragraphJson.text;
        }

        getText():string {
            return this.text;
        }

    }
}