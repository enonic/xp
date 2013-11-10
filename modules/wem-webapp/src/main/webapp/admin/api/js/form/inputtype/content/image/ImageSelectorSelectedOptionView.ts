module api_form_inputtype_content_image {

    export class ImageSelectorSelectedOptionView extends api_dom.DivEl{

        private option:ImageSelectorSelectedOption;

        constructor(option:ImageSelectorSelectedOption) {
            super( "ImageSelectorOptionView", "selected-option" );

            this.option = option;

            if( this.option.getContent() != null ) {

                this.doLayoutContent();
            }
            else {
                this.doLayoutAttachment();
            }
        }

        private doLayoutContent() {

            var content = this.option.getContent();

            this.getEl().setBackgroundImage("url(" + content.getIconUrl() + "?size=140&thumbnail=false)");

            var label = new api_dom.DivEl(null, "label");
            label.getEl().setInnerHtml(content.getName());
            this.appendChild(label);


        }

        private doLayoutAttachment() {

        }
    }
}