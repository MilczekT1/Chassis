### Release process

1. Create branch release/x.y.z
2. Update versions to x.y.z
3. Save SHA of the commit .
4. Merge to master
5. Tag commit with saved SHA
6. Push to origin to trigger release workflow.

All steps are covered in prepare-release.sh and tag-release.sh

- sh prepare-release.sh 0.4.2 0.4.3
- merge release/0.4.2 to master
- sh tag-release.sh 0.4.2

### Changelog:

##### 28.01.2024:

- Dependency management for lombok in bom.
- Jacoco verification during verify stage instead of test
- added @ControllerTest annotation to use in tests

##### 18.01.2024:

- Springboot 3.1.0 -> 3.2.1
- junit-5 5.9.3 -> 5.10.1
- updated mockito and assertj
- spring-cloud-dependencies 2022.0.3 -> 2023.0.0
- cucumber 7.6.0 -> 7.15.0
- Decommission password-management support in CI/CD
##### 08.06.2023:
- Springboot 3.0.4 -> 3.1.0
- junit-5 5.9.2 -> 5.9.3
- spring-cloud-dependencies 2022.0.1 -> 2022.0.3
##### 10.04.2023:
- Dependency management for test containers.
- Added missing auto configurations
- Removed dummy RestTools.java
##### 08.04.2023:
- Springboot 2.7.8 -> 3.0.4
- Added @ChassisApplication annotation. Should replace @SpringBootApplication
- Renamed chassis-settings module to chassis-bom (breaking change)
- Renamed Maven properties: chassis.dependencies.springboot.version -> chassis.springboot.version, chassis.dependencies.springcloudtrain.version -> chassis.springcloudtrain.version, chassis.dependencies.springcloud.version -> chassis.springcloud.version, chassis.dependencies.openapi.version -> chassis.openapi.version (breaking change)
- Dropped distributed tracing support.
- Upgrade of various dependencies
- Replaced embedded mongo dependency (as in spring migration guide)
```xml
<dependency>
  <groupId>de.flapdoodle.embed</groupId>
  <artifactId>de.flapdoodle.embed.mongo</artifactId>
  <version>3.5.1</version>
</dependency>
 ```
->
```xml
<dependency>
  <groupId>de.flapdoodle.embed</groupId>
  <artifactId>de.flapdoodle.embed.mongo.spring30x</artifactId>
  <version>4.6.2</version>
</dependency>
 ```
##### 6.07.2022:
- Springboot 2.4.9 -> 2.7.1 (one by one).
- Many dependencies upgrade.
- New parent for API modules.

##### 15.12.2021:
 - Extended actuator info endpoint. Example:

```json
{
    "git": {
        "branch": "develop",
        "commit": {
            "id": "ac9e2d7",
           "time": "2021-09-16T13:00:18Z"
        }
     },
    "build": {
        "springbootVersion": "2.4.9",
        "version": "0.7.0-SNAPSHOT",
        "artifact": "budget-management",
        "name": "budget-management",
        "time": "2021-12-15T18:57:44.209Z",
        "chassisVersion": "develop",
        "group": "pl.konradboniecki.budget"
    }
}
```
##### 25.07.2021:
 - Springboot 2.4.9
##### 31.07.2020:
 - Springboot 2.3.2.RELEASE
