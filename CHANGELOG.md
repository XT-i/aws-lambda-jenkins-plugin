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
