### Changelog:

##### 08.04.2023:
- Springboot 2.7.8 -> 3.0.4
- Added @ChassisApplication annotation. Should replace @SpringBootApplication
- Renamed chassis-settings module to chassis-bom (breaking change)
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
