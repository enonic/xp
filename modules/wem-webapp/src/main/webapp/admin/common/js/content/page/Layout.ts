module api_content_page{

    export class Layout extends Component<LayoutTemplateName> {

        constructor(builder:LayoutBuilder) {
            super(builder);
        }
    }

    export class LayoutBuilder extends ComponentBuilder<LayoutTemplateName>{

        public build():Page {
            return new Page(this);
        }
    }
}