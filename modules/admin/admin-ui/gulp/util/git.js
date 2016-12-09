const simpleGit = require('simple-git');
const path = require('path');

const root = path.resolve(__dirname, '../..');
const git = simpleGit(root);

function filterByExtension(files, extension) {
    if (extension) {
        return files.filter((file) => {
            const ext = path.parse(file).ext;
            return ext === extension;
        });
    }

    return files.slice(0);
}

// git status
function status(extension = '') {
    return new Promise((resolve) => {
        git.status((err, result) => {
            const files = result.files.map((file) => {
                return file.path;
            });

            resolve(filterByExtension(files, extension));
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

// git diff --name-only origin/master..HEAD
//   Outputs modified files from all commits that are not in master yet
function diff(branch = 'origin/master', extension = '') {
    const options = ['--name-only', `${branch}..HEAD`];
    return new Promise((resolve) => {
        git.diff(options, (err, result) => {
            const files = !result ? [] : result.trim().split('\n');

            resolve(filterByExtension(files, extension));
        });
    });
}


module.exports = {
    status,
    mergeBase,
    diff,
};