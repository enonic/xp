module api_content_page{

    export class Page extends Component<PageTemplateName> {

        constructor(builder:PageBuilder) {
            super(builder);
        }
    }

    export class PageBuilder extends ComponentBuilder<PageTemplateName>{

        public build():Page {
            return new Page(this);
        }
    }
}