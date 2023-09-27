# Test Suite for Apache Syncope

Test suite (unit test and integration test) for the Apache Syncope project, and analysis of their adequacy through coverage tools such as [Ba-Dua](https://github.com/saeg/ba-dua) for data coverage,  [JaCoCo](https://github.com/jacoco/jacoco) for statement coverage and branch coverage. Further quality controls of the developed tests were carried out through [Pitest](https://github.com/hcoles/pitest) (aka PIT) tool, to verify their robustness to SUT mutations.

[Mockito](https://github.com/mockito/mockito) was used to simulate the execution environment and [GitHub Actions](https://github.com/features/actions) was used as a continuous integration tool.



The following classes have been tested:
```
org.apache.syncope.core.spring.security.AuthDataAccessor

org.apache.syncope.core.spring.security.DefaultPasswordGenerator

org.apache.syncope.core.provisioning.api.utils.RealmUtils
```

Each tool is associated with a dedicated Maven profile, can be executed through the commands:

```
mvn clean verify -P jacoco
```
```
mvn clean verify -P badua
```
```
mvn clean test -P pit
```


## Apache Syncope Overview

Apache Syncope is an Open Source system for managing digital identities in enterprise environments, 
implemented in Java EE technology and released under Apache 2.0 license.

## Some results of adequacy

|         Class        | Statement Coverage | Branch Coverage | Mutation Coverage |
|:------------------------:|:----------------------:|:-------------------:|:---------------------:|
| DefaultPasswordGenerator |           96%          |         86%         |          36%          |
| RealmUtils               |          100%          |         95%         |          78%          |
| buildAuthorities         |          100%          |         100%        |                       |

## Documentation

[Testing report](https://github.com/callbrok/TestSuite_Syncope/blob/61f3be10052d3f14492202c2af7c4960924e22a1/report/testing_report.pdf) (:it:) made with all the results, improvements and considerations.



