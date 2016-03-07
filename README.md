# git-obsolete-branch-remover

Lists or removes local or remote Git obsolete branches.

## Usage:


* Usage pattern:

``` gobr [number of days to obsolete day] [--list|--remove|--forceremove] [--local|--remote] [private key] ```


* List local branches with last commit older than 30.

``` gobr 30 ```


* Remove branches even if they were not merged into base branch (remote/local develop/master).

``` gobr 30 --forceremove ```


* Remove remote branches with last commit older than 30 days using passed private key.

``` gobr 30 --remove --remote ~/.ssh/id_rsa ```
