# git-problem-notifier

Project leveraging JGit aiming to notify of obsolete branches or conflicts between currently in developement branches.

## Usage examples:

* List obsolate branches without fetching.
``` gpn [number of days to obsolete day] ```

* List obsolate branches with fetch using identity key file.
``` gpn -i [key file path] [number of days to obsolete day] ```
