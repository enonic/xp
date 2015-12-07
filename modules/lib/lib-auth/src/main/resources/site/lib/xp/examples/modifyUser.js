// Callback to edit the user.
function editor(c) {
    c.displayName = 'Modified display name';
    c.email = "new_email@enonic.com";
    return c;
}

// Modify user with specified key.
var user = authLib.modifyUser({
    key: 'user:enonic:userId',
    editor: editor
});
