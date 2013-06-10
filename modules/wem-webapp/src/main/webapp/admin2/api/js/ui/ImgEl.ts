module api_ui {

    export class ImgEl extends Component {

        private static constructorCounter:number = 0;

        private el:HTMLElementHelper;

        private id:string;

        constructor(name:string) {
            super( name, "img" );
            this.el = HTMLImageElementHelper.create();
        }

        getImg():HTMLImageElementHelper {
            return <HTMLImageElementHelper>this.el;
        }
    }
}
