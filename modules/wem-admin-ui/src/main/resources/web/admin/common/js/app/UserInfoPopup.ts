module api.app {

    export class UserInfoPopup extends api.dom.DivEl {

        constructor() {
            super('user-info-popup');

            this.createContent();

            api.dom.Body.get().appendChild(this);
        }

        toggle() {
            this.isVisible() ? this.hide() : this.show();
        }

        private createContent() {
            var userName = 'Thomas Lund Sigdestad',
                photoUrl = api.util.getAdminUri('common/images/tsi-profil.jpg'),
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
    }

}