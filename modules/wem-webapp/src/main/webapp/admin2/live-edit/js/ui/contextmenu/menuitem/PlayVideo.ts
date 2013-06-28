module LiveEdit.ui.contextmenu.menuitem {
    var $ = $liveEdit;

    export class PlayVideo extends LiveEdit.ui.contextmenu.menuitem.Base {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init():void {
            var $button = this.createButton({
                text: 'Play Video',
                id: 'live-edit-button-play-video',
                handler: (event) => {

                    // fixme: remove. Play all videos for now :)
                    $('video', this.menu.selectedComponent).each(function (i, videoDomEl) {
                        videoDomEl.play();
                    });

                    event.stopPropagation();
                }
            });

            this.appendTo(this.menu.getRootEl());
            this.menu.buttons.push(this);
        }
    }
}