var assert = require('/lib/xp/testing.js');
var repoLib = require('/lib/xp/repo.js');

exports.refreshDefault = function () {

    repoLib.refresh();

};

exports.refreshAll = function () {

    repoLib.refresh({mode: 'all'});

};

exports.refreshSearch = function () {

    repoLib.refresh({mode: 'search'});

};

exports.refreshStorage = function () {

    repoLib.refresh({mode: 'storage'});

};

exports.refreshInvalid = function () {

    repoLib.refresh({mode: 'stuff'});

};