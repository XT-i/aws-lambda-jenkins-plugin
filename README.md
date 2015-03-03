# aws-lambda-jenkins-plugin

This plugins adds a post build step that uploads a file or folder to AWS Lambda.

- Create AWS IAM user for use with this plugin. Must have permissions to upload a Lambda deployment package.
- Select "Deploy into Lambda" under "Add post-build step"
- Fill in the fields (description is optional)
  - For more info click on the question marks next to the input fields

When the Lambda deployment package has been uploaded succesfully a Lambda icon will appear on the build page.
