## 0.5.5

Features:

    - Added support for Jenkins managed proxy configuration

## 0.5.4

Features:

    - Added a separate deploy version and update Lambda alias build step

## 0.5.3

Bugfixes:

    - Fixed problem where zip functionality would continue until full disk if folder was root of workspace. [Issue 41](https://github.com/XT-i/aws-lambda-jenkins-plugin/issues/41)
    
## 0.5.2

Features:

    - Allow changing runtime environment.

## 0.5.1 (2016-03-14)

Bugfixes:

    - Fixed non serializable JsonParameter to allow distributed build with Lambda invocation output directed into environment variables

## 0.5.0 (2016-02-13)

Features:

    - Jenkins Pipeline support (General Build Step)

## 0.4.3 (2016-02-26)
    
Bugfixes:

    - Empty timeout and memory no longer throws a NumberFormatException as they are not required when updating code only.

## 0.4.2 (2016-02-26)
    
Bugfixes:

    - Now using workspace to store temporary artifact file instead of system temporary folder.

## 0.4.1 (2016-02-13)

Features:

    - AWS Lambda VPC support
    - Advanced block to reduce screen space used. Preparation for move to @DataboundSetter
    
Bugfixes:

    - Job success only toggle fixed for event source configuration
