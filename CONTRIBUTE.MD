## Want to contribute ?

Of course, you can submit pull requests. Pull requests are really appreciate for bug fixing. So, don't hesitate.

## How to prepare a release ?

Project versioning is supported by Maven release plugin. This plugin updates the parent project and all its modules versions.

In a standard environnement, perform release is done directly on CI server (such as Jenkins). Here, we delegate compilation, packaging and deployment to Travis CI online service.

On Travis, a best practice to create a dedicated release branch or tag. Travis will detect it and make everything.
Hopefully, the Maven release plugin creates a tag when it updates project version. 

So, to make a release, run the following command lines from your computer :
```
git reset --hard
git pull --rebase
mvn clean release:clean release:prepare
git push
```
This will update version and create a tag detected by Travis.
