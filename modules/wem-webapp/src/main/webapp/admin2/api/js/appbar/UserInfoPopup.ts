module api_appbar {

    export class UserInfoPopup extends api_dom.DivEl {

        private isShown:bool = false;

        constructor() {
            super('UserInfoPopup', 'user-info-popup');

            this.createContent();
            this.render();
        }

        private createContent() {
            var userName = 'Thomas Lund Sigdestad',
                photoUrl = api_util.getAbsoluteUri('admin/resources/images/tsi-profil.jpg'),
                qName = 'system/tsi';

            var content = '<div class="title">User</div>' +
                          '<div class="user-name">' + userName + '</div>' +
                          '<div class="content">' +
                          '<div class="column">' +
                          '<img src="' + photoUrl + '"/>' +
                          '<button>Log Out</button>' +
                          '</div>' +
                          '<div class="column">' +
                          '<span>' + qName + '</span>' +
                          '<a href="#">View Profile</a>' +
                          '<a href="#">Edit Profile</a>' +
                          '<a href="#">Change User</a>' +
                          '</div>' +
                          '</div>';

            this.getEl().setInnerHtml(content);
        }

        private render() {
            this.hide();
            this.isShown = false;
            document.body.insertBefore(this.getHTMLElement());
        }

        toggle() {
            this.isShown ? this.hide() : this.show();
            this.isShown = !this.isShown;
        }
    }

}