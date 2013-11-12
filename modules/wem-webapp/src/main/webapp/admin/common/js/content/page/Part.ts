module api_content_page{

    export class Part extends Component<PartTemplateName>{

        constructor(builder:PartBuilder) {
            super(builder);
        }
    }

    export class PartBuilder extends ComponentBuilder<PartTemplateName>{

        public build():Page {
            return new Page(this);
        }
    }
}