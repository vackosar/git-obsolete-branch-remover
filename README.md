# git-obsolete-branch-remover

Lists or removes local or remote Git branches based on last commit date and merge status.

## Usage:

    usage:   gpn [number of days to obsolete day] [OPTIONS]
     -all,--all
     -forceremove,--forceremove
     -k,--key <path to private key>
     -list,--list
     -local,--local
     -remote,--remote
     -remove,--remove


* List local branches with last commit older than 30:

 ``` gobr 30 ```


* Remove even unmerged branches (unmerged into base branch remote/local develop/master):

 ``` gobr 30 --forceremove ```


* Remove remote branches with last commit older than 30 days using passed private key:

  ``` gobr 30 --remove --remote --key ~/.ssh/id_rsa ```

## Execution with Maven with automatic dependency management:

- Execute following Maven POM goal "validate".

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>exec</groupId>
    <artifactId>exec</artifactId>
    <version>1</version>
    <dependencies>
        <dependency>
            <groupId>com.vackosar.gitobsoletebranchremover</groupId>
            <artifactId>git-obsolete-branch-remover</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>1.2.1</version>
                <executions>
                    <execution>
                        <phase>validate</phase>
                        <goals>
                            <goal>java</goal>
                        </goals>
                        <configuration>
                            <mainClass>com.vackosar.gitobsoletebranchremover.boundary.Main</mainClass>
                            <arguments>
                                <argument>1</argument>
                            </arguments>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```