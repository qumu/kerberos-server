# Kerberos Server

This project shows spins up a server which accepts authentication/validation for a Kerberos HTTP call. The call presents a service ticket against a common Domain Controller. Spring Boot and Spring Security Kerberos project are used. This example is based on https://github.com/spring-projects/spring-security-kerberos/tree/master/spring-security-kerberos-samples/sec-server-win-auth.

The main differences are:

* Once the ticket is successfully validated, no LDAP or active directory lookup is performed for the principal/username.
* The server must run in a host which name matches a user in the Kerberos Domain Controller
* A custom entrypoint is provided `CustomSpnegoEntryPoint` which returns 401 status code for failures / non-authenticated requests
* JSON based API is provided with a public `/status` endpoint and a protected `hello` endpoint

## Requirements

* Create a user (principal) in Windows Active Directory to represent this server. In the `Account` tab set:

  - `SERV/yourusername.domain.com` as `User logon name`
  - `yourusername` as `User logon name (pre-Windows 2000)` if present

* In Windows, as Administrator, set an SPN for that user:

```
setspn -A SERV/yourusername.domain.com yourusername
```

* Then generate a `keytab` file:

```
ktpass /out C:\yourusername.keytab /mapuser yourusername@DOMAIN.COM /princ SERV/yourusername.domain.com@DOMAIN.COM /pass yourpassword /kvno 0
```

* Install and configure Kerberos in the host machine. This an example of `/etc/krb5.conf`:

```
[libdefaults]
    default_realm = DOMAIN.COM
    default_tkt_enctypes = arcfour-hmac-md5 des-cbc-crc des-cbc-md5
    default_tgs_enctypes = arcfour-hmac-md5 des-cbc-crc des-cbc-md5
    ticket_lifetime = 24h
    forwardable = yes
    dns_lookup_kdc = false

[realms]
    DOMAIN.COM = {
        kdc = yourActiveDirectoryHost.domain.com
        default_domain = domain.com
    }

[domain_realm]
    .domain.com = DOMAIN.COM

[logging]
 krb5 = SYSLOG:
 default = FILE:/var/logs/krb5.log
 admin_server = FILE:/var/logs/krb5.log
 kdc = FILE:/var/logs/krb5.log
```

* Ensure the `kdc` (Windows Active Directory domain in this case) is accessible from the current host


## Run the server


* Copy the `yourusername.keytab` into a location in the machine running the server. Then, in `application.yml`:

 - `port`: the port that the embedded Tomcat server listens on
 - `keytab-location`: location of the keytab file in the server machine
 - `service-principal`: fully qualified SPN of the created user, i.e. `SERV/yourusername.domain.com@DOMAIN.COM`

* Generate the JAR file:

```
./gradlew assemble
```

* Execute the server:

```
java -jar build/libs/kerberos-server-0.1.jar
```

* Tomcat will be launched in the specified port, and the status endpoint should be visible `http://localhost:port/status`

* Config file `application.yml` can be overriden at execution time. Just provide a file with the same name in directory the previous command in executed from (not where the JAR lives) or give an extra command line argument pointing
to a specific config file:

```
java -jar build/libs/kerberos-client-0.1.jar --spring.config.location=/path/to/propertiesFile.yml
```






