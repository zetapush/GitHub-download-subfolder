# GitHub Download Subfolder

There are many online tools to download a zip of a subfolder from a GitHub repository.
So all of them create the zip file with the front code.

> Issue : We can't the the generated file with a tool like _curl_

This repository do the same stuff, but the generation of the file is on the back end side.

## How to use it ?

There are 3 properties :

- **owner** : The owner of the repository on GitHub
- **repository** : Name of the repository on GitHub
- **path** (optional) : Path of the subfolder. If the property is missing or is empty, the tool get the entire repository

### From _curl_

`curl -X GET 'http://github-download-subfolder.zpush.io/?owner=zetapush&repository=zetapush-tutorials&path=avengersChat' --output avengersChat.zip`
