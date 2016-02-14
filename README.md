# git-problem-notifier

Project leveraging JGit aiming to notify of obsolete branches or conflicts between currently in developement branches.

## Usage:

* Usage pattern:
``` gpn [number of days to obsolete day] [--list|--remove] [--local|--remote] [unencrypted private key] ```

* List remote branches with last commit older than 30 days without fetching.
``` gpn 30 ```

* Remove remote branches with last commit older than 30 days after fetching using passed unencrypted key.
``` gpn 30 --remove --remote ~/.ssh/id_rsa ```
