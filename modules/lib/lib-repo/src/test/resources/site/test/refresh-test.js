var assert = require('/lib/xp/assert.js');
var repoLib = require('/lib/xp/repo.js');

exports.refreshDefault = function () {

    repoLib.refresh();

};

exports.refreshAll = function () {

    repoLib.refresh('all');

};

exports.refreshSearch = function () {

    repoLib.refresh('search');

};

exports.refreshStorage = function () {

    repoLib.refresh('storage');

};

exports.refreshInvalid = function () {

    repoLib.refresh('stuff');

};