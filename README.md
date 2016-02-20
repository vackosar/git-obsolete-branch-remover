# git-obsolete-branch-remover

Lists or removes local or remote Git branches.

## Usage:

* Usage pattern:
``` gobr [number of days to obsolete day] [--list|--remove] [--local|--remote] [unencrypted private key] ```

* List local branches with last commit older than 30.
``` gobr 30 ```

* Remove remote branches with last commit older than 30 days using passed unencrypted key.
``` gobr 30 --remove --remote ~/.ssh/id_rsa ```
