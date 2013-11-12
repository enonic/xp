module api_content_page{

    export class Image extends Component<ImageTemplateName> {

        constructor(builder:ImageBuilder) {
            super(builder);
        }
    }

    export class ImageBuilder extends ComponentBuilder<ImageTemplateName>{

        public build():Image {
            return new Page(this);
        }
    }
}