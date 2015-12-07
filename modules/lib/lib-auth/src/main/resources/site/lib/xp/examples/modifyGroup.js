// Callback to edit the group.
function editor(c) {
    c.displayName = 'Modified display name';
    return c;
}

// Modify group with specified key.
var group = authLib.modifyGroup({
    key: 'group:enonic:groupId',
    editor: editor
});
