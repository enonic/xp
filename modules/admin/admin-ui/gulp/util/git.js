const simpleGit = require('simple-git');
const path = require('path');

const root = path.resolve(__dirname, '../..');
const git = simpleGit(root);

function filterByExtension(files, extension, filter) {

    if (extension) {
        return files.filter((file) => {
            const ext = path.parse(file).ext;
            return ext === extension && !(file.search(filter) === -1);
        });
    }

    return files.slice(0);
}

// git status
function status(extension = '', filter = /./) {
    return new Promise((resolve) => {
        git.status((err, result) => {
            const files = result.files.map((file) => {
                return file.path;
            });

            resolve(filterByExtension(files, extension, filter));
        });
    });
}

// git merge-base master HEAD
function mergeBase() {
    return new Promise((resolve) => {
        git.raw(['merge-base', 'master', 'HEAD'], (err, commitHash) => {
            resolve(commitHash.trim());
        });
    });
}

// git diff --name-only origin/master
//   Outputs modified files from all commits that are not in master yet and indexed files
function diff(branch = 'origin/master', extension = '', filter = /./) {
    const options = ['--name-only', `${branch}`];
    return new Promise((resolve) => {
        git.diff(options, (err, result) => {
            const files = !result ? [] : result.trim().split('\n');

            resolve(filterByExtension(files, extension, filter));
        });
    });
}


module.exports = {
    status,
    mergeBase,
    diff,
};