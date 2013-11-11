module api_page{

    export class ComponentFactory {

        static createComponent(componentJson:api_page_json.ComponentJson):Component {
            if (componentJson.componentType == "Image") {
                return new Image(<api_page_json.ImageJson>componentJson);
            }
            else if (componentJson.componentType == "Video") {
                return new Video(<api_page_json.VideoJson>componentJson);
            }
            else if (componentJson.componentType == "Paragraph") {
                return new Paragraph(<api_page_json.ParagraphJson>componentJson);
            }
            else if (componentJson.componentType == "Part") {
                return new Part(<api_page_json.PartJson>componentJson);
            }
            else {
                throw new Error("Unsupported Component: " + componentJson.componentType);
            }
        }
    }
}