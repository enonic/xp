module api_content_page{

    export class Layout extends Component<LayoutTemplateName> {

        private config:api_data.RootDataSet;

        constructor(builder:LayoutBuilder) {
            super(builder);
        }

        getConfig():api_data.RootDataSet {
            return this.config;
        }
    }

    export class LayoutBuilder extends ComponentBuilder<LayoutTemplateName>{

        config:api_data.RootDataSet;

        public setConfig(value:api_data.RootDataSet):LayoutBuilder {
            this.config = value;
            return this;
        }

        public build():Layout {
            return new Layout(this);
        }
    }
}